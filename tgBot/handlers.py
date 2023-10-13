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
import taskPreparer
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
class CreateSubject(StatesGroup):
    waitingForSubjectName = State()
class DeleteSubject(StatesGroup):
    waitingForDeletingSubject = State()
class CreateTask(StatesGroup):
    pass
@r.message(Command("start"))
async def start(message: types.Message, state: FSMContext):
    await state.clear()
    await message.answer("Привет, если у тебя уже есть учетная запись, напиши /login, иначе /register😁\nчтобы отменить текущее действие, напиши /cancel")
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
@r.message(F.text.lower().startswith("добавь"))
async def addTask(message: types.Message, state: FSMContext):
    if taskPreparer.taskIsValid(message.text,db):
        l = taskPreparer.prepareTask(message.text)
        db.addTask(l)
        await message.answer(f"На {l[1]} число по предмету {l[0]} добавлено задание {l[2]}")
    else:await message.answer("Ты неверно описал задание, формат:\n Добавь (Название предмета) на (дата вида 00.00) (описание задания)\nСкобки не нужны😉")
@r.message(Command("newsub"))
async def addSubject(message: types.Message, state: FSMContext):
    t = message.text
    if len(t.split())==1:
        await message.answer("Введи название предмета🥸")
        await state.set_state(CreateSubject.waitingForSubjectName)
        return
    else:
        name = " ".join(t.split()[1:]).lower()
        db.addSub(name)
        await message.answer(f"Предмет {name} добавлен🥲")
@r.message(CreateSubject.waitingForSubjectName)
async def addSubjectByName(message: types.Message, state: FSMContext):
    t = message.text
    name = " ".join(t.split()[1:]).lower()
    db.addSub(name.capitalize())
    await message.answer(f"Предмет {name} добавлен🥲")
    await state.clear()
@r.message(DeleteSubject)
async def prepToDelSubject(message: types.Message, state: FSMContext):
    subjects = db.getSubjectNames()
    buttons = [[types.InlineKeyboardButton(text = i[1], callback_data=f"DS {i[0]}")] for i in subjects]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    await message.answer("Выбери предмет для удаления🫠",reply_markup=kb)
@r.message(F.data.startswith("DS"))
async def delSubject(data:types.CallbackQuery):
    subject = int(data.message.text.split()[3:])
    db.delSubByID(subject)
    await data.answer(f"Предмет {subject} удален😁")



