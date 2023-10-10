#начать работу над добавлением задач

import logging
from aiogram import Router,F
from aiogram import types
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import dbWorker
from aiogram.fsm.state import StatesGroup, State
import re
from cfg import DB_PATH
r = Router()
db = dbWorker.dataBaseWorker(DB_PATH)
logging.basicConfig(level=logging.INFO)
class Register(StatesGroup): # - step by step registration
    waitingForKey = State()
    waitingForUserName = State()
    waitingForPassword = State()

class Login(StatesGroup):
    waitingForLogin = State()
    waitingForPassword = State()
@r.message(Command("start"))
async def start(message: types.Message, state: FSMContext):
    await state.clear()
    await message.answer("Привет, если у тебя уже есть учетная запись, напиши /login, иначе /register😁\nчтобы отменить текущее действие напиши /cancel")

@r.message(Command("register"))
async def register(message: types.Message, state: FSMContext):
    await state.clear()
    if db.isUserTG(message.chat.id):
        await message.answer("Вы уже авторизованы😌")
        return
    await message.answer("Введите ключ доступа для продолжения работы🙃\nНапишите /login,если у вас уже есть учетная запись")
    await state.set_state(Register.waitingForKey)
@r.message(Command("cancel"))
async def cancel(message: types.Message, state: FSMContext):
    await state.clear()
    await message.answer("Текущее действие отменено🗑️")
@r.message(Register.waitingForKey)
async def verifyKey(message: types.Message, state: FSMContext):
    if not db.verifyOneTimeKey(message.text):
        await message.answer("Неверный ключ, попробуй еще раз🙄")
        return
    await state.update_data(usedKey = message.text)
    await message.answer("Введите имя пользователя"
                  "\nНапример: NaGiBaTor228"
                  "\nНе используйте спецсимволы и пробелы"
                  "\nДлина до 64 символов")
    await state.set_state(Register.waitingForUserName)

@r.message(Register.waitingForUserName)
async def setUpUserName(message: types.Message, state: FSMContext):
    if not re.fullmatch(r"[\w\d]{4,64}",message.text):
        await message.answer("Имя пользователя не соответствует требованиям☹️")
        return
    await state.update_data(userName = message.text)
    await message.answer("Придумайте сложный пароль🤓")
    await state.set_state(Register.waitingForPassword)

@r.message(Register.waitingForPassword)
async def setUpPassword(message: types.Message, state: FSMContext):
    if not re.fullmatch(r".{8,1024}",message.text):
        await message.answer("Пароль слишком короткий🫡")
        return
    userData = await state.get_data()
    db.delOneTimeKey(userData["usedKey"])
    db.addUser(userData["userName"], message.chat.id, message.text, 0)
    await message.answer("Регистрация прошла успешно😎")
    await state.clear()

@r.message(Command("login"))
async def login(message: types.Message, state: FSMContext):
    await state.clear()
    if db.isUserTG(message.chat.id):
        await message.answer("Вы уже авторизованы")
        return
    await message.answer("Введите логин")
    await state.set_state(Login.waitingForLogin)

@r.message(Login.waitingForLogin)
async def parseLogin(message: types.Message, state: FSMContext):
    if not db.isUserNAME(message.text):
        await message.answer("Пользователь не найден❎\nПопробуйте еще раз")
        return
    await message.answer("Пользователь найден✅\nВведите пароль")
    await state.update_data(userName = message.text)
    await state.set_state(Login.waitingForPassword)

@r.message(Login.waitingForPassword)
async def parsePassword(message: types.Message, state: FSMContext):
    userData = await state.get_data()
    if not db.checkPassword(userData["userName"],message.text):
        await message.answer("Пароль неверный🫤")
    db.addTelegramToExisting(message.chat.id,userData["userName"])
    await message.answer("Авторизация прошла успешно🙃")
    state.clear()
    return




