#доделать state.set_data
#доделать добавление пользователя в БД
#начать работу над добавлением задач

import logging

from aiogram import Router
from aiogram import types
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import dbWorker
from aiogram.fsm.state import StatesGroup, State
import re
from cfg import DB_PATH
r = Router()
db = dbWorker.dataBaseWorker(DB_PATH)
class Register(StatesGroup): # - step by step registration
    waitingForKey = State()
    waitingForUserName = State()
    waitingForPassword = State()
@r.message(Command("start"))
async def start(msg: types.Message, state: FSMContext):
    await msg.answer("Введите ключ доступа для продолжения работы🙃")
    await state.set_state(Register.waitingForKey)

@r.message(Register.waitingForKey)
async def verifyKey(message: types.Message, state: FSMContext):
    if not db.verifyOneTimeKey(message.text):
        await message.answer("Неверный ключ, попробуй еще раз🙄")
        await state.set_state(Register.waitingForKey)
        return
    await message.answer("Введите имя пользователя"
                  "\nНапример: NaGiBaTor228"
                  "\nНе используйте спецсимволы и пробелы"
                  "\nДлина до 64 символов")
    await state.set_state(Register.waitingForUserName)

@r.message(Register.waitingForUserName)
async def setUpUserName(message: types.Message, state: FSMContext):
    if not re.fullmatch(r"[\w\d]{4,64}",message.text):
        await message.answer("Имя пользователя не соответствует требованиям☹️")
        await state.set_state(Register.waitingForUserName)
        return
    await message.answer("Придумайте сложный пароль🤓")
    await state.set_state(Register.waitingForPassword)

@r.message(Register.waitingForPassword)
async def setUpPassword(message: types.Message, state: FSMContext):
    if not re.fullmatch(r".{8,1024}",message.text):
        await message.answer("Пароль слишком короткий🫡")
        await state.set_state(Register.waitingForPassword)
        return
    data = state.get_data()
    print(data)
    db.addUser()
    await message.answer("Регистрация прошла успешно😎")
