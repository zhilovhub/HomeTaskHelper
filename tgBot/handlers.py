import logging
from aiogram import Router,F
from aiogram import types
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import dbWorker
from aiogram.fsm.state import StatesGroup, State
import re
from cfg import DB_PATH
import matcher
r = Router()
db = dbWorker.dataBaseWorker(DB_PATH)
logging.basicConfig(level=logging.INFO)
users = db.getUsers()

class Register(StatesGroup):
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


class EditTask(StatesGroup):
    waitingForDescription = State()
    waitingForDate = State()
    waitingForBoth = State()


class AddAlias(StatesGroup):
    waitingForSubjectNAlias = State()
    waitingForSubject = State()
    waitingForAlias = State()


#-------------------MISC-------------------
@r.message(Command("start"))
async def start(message: types.Message, state: FSMContext):
    await message.answer("Привет, если у тебя уже есть учетная запись, напиши /login, иначе /register")

@r.message(Command("help"))
async def help(message: types.Message, state: FSMContext):
    await message.answer("""
    Расскажу коротко обо всех доступных тебе командах😁
    /newsub (название предмета) - добавляет новый предмет для тебя и твоей группы🥲
    /delsub - показывает меню, выбрав элемент которого будет удален соответствующий предмет
    /addalias - добавляет синоним для предмета, чтобы ты мог написать как линал, так и линейка или ЛиНеЙнАя АлГеБра🤓
    /listtasks - показывает всю домашку
    /cancel - отменяет текущее действие
    
    Чтобы добавить домашку отправь сообщение в следующем формате:
    Добавь (название предмета) на (дата вида ДД.ММ) (текст задания)
    Например: Добавь матан на 18.10 подготовка к контрольной
    """)
    pass


@r.message(Command("cancel"))
async def cancel(message: types.Message, state: FSMContext):
    await state.clear()
    await message.answer("Текущее действие отменено🗑️")
    return
#------------------END------------------


  
  
#------------------REGISTATION------------------
@r.message(Command("register"))
async def registerNewUser(message: types.Message, state: FSMContext):
    await state.clear()
    if db.isUserTG(message.chat.id):
        await message.answer("Вы уже авторизованы😌")
        return
    await message.answer("Введите ключ доступа для продолжения работы🙃\nНапишите /login,если у вас уже есть учетная запись")
    await state.set_state(Register.waitingForKey)


@r.message(Register.waitingForKey)
async def verifyKey(message: types.Message, state: FSMContext):
    if not db.verifyOneTimeKey(message.text):
        await message.answer("Неверный ключ, попробуй еще раз🙄")
    else:
        await state.update_data(usedKey = message.text)
        await message.answer("Введите имя пользователя"
                      "\nНапример: NaGiBaTor228"
                      "\nНе используйте спецсимволы и пробелы"
                      "\nДлина до 64 символов")
        await state.set_state(Register.waitingForUserName)


@r.message(Register.waitingForUserName)
async def setUpUserName(message: types.Message, state: FSMContext):
    if not matcher.isUserNameValid(message.text):
        await message.answer("Имя пользователя не соответствует требованиям☹️")
        return
    await state.update_data(userName = message.text)
    await message.answer("Придумайте сложный пароль🤓")
    await state.set_state(Register.waitingForPassword)


@r.message(Register.waitingForPassword)
async def setUpPassword(message: types.Message, state: FSMContext):
    if not matcher.isPasswordValid(message.text):
        await message.answer("Пароль слишком короткий 🍆 🫡")
        return
    await message.delete()
    userData = await state.get_data()
    db.delOneTimeKey(userData["usedKey"])
    db.addUser(userData["userName"], message.chat.id, message.text, 0)
    global users
    users.append(message.chat.id)
    await message.answer("Регистрация прошла успешно😎")
    await state.clear()
#------------------END------------------




#------------------LOGIN------------------
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
        return
    db.addTelegramToExisting(message.chat.id,userData["userName"])
    await message.delete()
    await message.answer("Авторизация прошла успешно🙃")
    await state.clear()
    return
#------------------END------------------




#------------------Task------------------
@r.message(F.text.lower().startswith("добавь"),F.from_user.id.in_(users))
async def addTask(message: types.Message, state: FSMContext):
    if matcher.isTaskValid(message.text, db):
        subject,date,description = matcher.prepareTask(message.text)
        if matcher.isSubjectExists(subject, db):
            db.addTask(subject,date,description)
            await message.answer(f"На {date} число по предмету {subject} добавлено задание {description}")
            return
    await message.answer("Ты неверно описал задание или предмета не существует, формат:\nДобавь (Название предмета) на (дата вида 00.00) (описание задания)")


@r.message(Command("deltask"),F.from_user.id.in_(users))
async def prepToDelTask(message: types.Message, state: FSMContext):
    tasks = db.getTasks(db.getUserNameByTGID(message.from_user.id))
    buttons = [[types.InlineKeyboardButton(text = i[1],callback_data=f"DT {i[0]}")] for i in tasks]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    if len(tasks)!=0: await message.answer("Выбери задание для удаления",reply_markup=kb)
    else: await message.answer("Заданий нет😁")


@r.callback_query(F.data.startswith("DT"))
async def delTask(data: types.CallbackQuery):
    taskID = int(data.data.split()[1])
    print(taskID)
    db.delTask(taskID)
    await data.message.edit_text("Задание удалено😎")


@r.message(Command("listtasks"),F.from_user.id.in_(users))
async def displayTasks(message: types.Message, state: FSMContext):
    txt = db.getUncompletedTasks(db.getUserNameByTGID(message.from_user.id))
    await message.answer("Задачи:\n"+txt)


@r.message(Command("edittask"),F.from_user.id.in_(users))
async def prepToEditTask(message: types.Message, state: FSMContext):
    tasks = db.getTasks(db.getUserNameByTGID(message.from_user.id))
    buttons = [[types.InlineKeyboardButton(text=i[1], callback_data=f"ETST {i[0]}")] for i in tasks]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    if len(tasks)!=0: await message.answer("Выбери задание для изменения😶",reply_markup=kb)
    else: await message.answer("Нечего редактировать, все выполнено!🥳")


@r.callback_query(F.data.startswith("ETST"))
async def editTask(data: types.CallbackQuery, state: FSMContext):
    ID = int(data.data.split()[1])
    buttons = [[types.InlineKeyboardButton(text = i, callback_data = f"ETET {a}")] for a,i in enumerate(["описание","дата"])]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    await data.message.delete()
    await state.update_data(taskID = ID)
    await data.message.answer("Что ты хочешь изменить?",reply_markup=kb)


@r.callback_query(F.data.startswith("ETET"))
async def selectEditingParameter(data: types.CallbackQuery, state: FSMContext):
    editingType = int(data.data.split()[1])
    await data.message.delete()
    if editingType == 0:
        await data.message.answer("Введи новое описание задачи🗣")
        await state.set_state(EditTask.waitingForDescription)
    elif editingType == 1:
        await data.message.answer("Введи новый дедлайн задачи📆")
        await state.set_state(EditTask.waitingForDate)

@r.message(EditTask.waitingForDescription)
async def editTaskDescription(message: types.Message, state: FSMContext):
    userData = await state.get_data()
    print(userData)
    db.updateTask(task_id = userData["taskID"],description=message.text.capitalize())
    await message.answer("Задание обновлено")
    await state.clear()


@r.message(EditTask.waitingForDate)
async def editTaskDate(message: types.Message, state: FSMContext):
    if not matcher.isDateValid(message.text):
        await message.answer("Ты ввел дату в неверном формате🤡"); return
    data = await state.get_data()
    db.updateTask(task_id = data["taskID"], date = message.text.capitalize())
    await message.answer("Задание обновлено😵‍💫")
    await state.clear()


@r.message(Command("setcomplete"),F.from_user.id.in_(users))
async def prepToMarkAsComplete(message: types.Message, state: FSMContext):
    tasks = db.getTasks(db.getUserNameByTGID(message.from_user.id))
    buttons = [[types.InlineKeyboardButton(text=x[1], callback_data=f"MC {x[0]}")] for x in tasks]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    await message.answer("Выбери задание, которое нужно отметить как выполненное",reply_markup=kb)

@r.callback_query(F.data.startswith("MC"))
async def MarkAsComplete(data: types.CallbackQuery, state: FSMContext):
    taskID = int(data.data.split()[1])
    db.markAsComplete(taskID, db.getUserNameByTGID(data.from_user.id))
    await data.message.delete()
    await data.message.answer("Молодец, задание вычеркнуто из списка дел✍️")
#------------------END------------------




#------------------Subject------------------
@r.message(Command("newsub"),F.from_user.id.in_(users))
async def addSubject(message: types.Message, state: FSMContext):
    t = message.text
    if len(t.split())==1:
        await message.answer("Введи название предмета🥸")
        await state.set_state(CreateSubject.waitingForSubjectName)
        return
    else:
        name = matcher.prepareSub(t).lower()
        db.addSub(name)
        await message.answer(f"Предмет {name} добавлен🥲")


@r.message(CreateSubject.waitingForSubjectName)
async def addSubjectByName(message: types.Message, state: FSMContext):
    t = message.text.lower()
    db.addSub(t)
    await message.answer(f"Предмет {t} добавлен🥲")
    await state.clear()


@r.message(Command("delsub"),F.from_user.id.in_(users))
async def prepToDelSubject(message: types.Message, state: FSMContext):
    subjects = db.getSubjectNamesAndIDs()
    buttons = [[types.InlineKeyboardButton(text = i[1], callback_data=f"DS {i[0]}")] for i in subjects]
    kb = types.InlineKeyboardMarkup(inline_keyboard=buttons)
    await message.answer("Выбери предмет для удаления🫠",reply_markup=kb)


@r.message(AddAlias.waitingForSubjectNAlias)
async def addAlias(message: types.Message, state: FSMContext):
    subject_name, alias = matcher.prepareAlias(message.text)
    print(subject_name, alias)
    reply = db.aliasIsValid(subject_name,alias)
    if reply==0:
        db.addAlias(subject_name,alias)
        await message.answer(f"Добавлен синоним {alias} для предмета {subject_name}")
        await state.clear()
        return
    elif reply==1: await message.answer("Такой предмет не найден")
    elif reply==2: await message.answer("Такой синоним уже существует")


@r.callback_query(F.data.startswith("DS"),F.from_user.id.in_(users))
async def delSubject(data:types.CallbackQuery):
    subject = int(data.data.split()[1])
    db.delSubByID(subject)
    await data.message.edit_text(f"Предмет удален😁",reply_markup=None)


@r.message(Command("addalias"),F.from_user.id.in_(users))
async def startAddAlias(message: types.Message, state: FSMContext):
    await message.answer("Отправь мне название предмета, для которого ты хочешь добавить синоним,\nИ сам синоним в одном сообщении (через запятую)🤯")
    await state.set_state(AddAlias.waitingForSubjectNAlias)
#------------------END------------------



