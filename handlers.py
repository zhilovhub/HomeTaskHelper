from aiogram import Router
from aiogram.filters import Command, state
from aiogram import types
from aiogram.filters import Command
from aiogram.fsm.context import FSMContext
import asyncio
import dbWorker
from aiogram.fsm.state import StatesGroup, State

r = Router()
db = dbWorker.DataBase()
class Register(StatesGroup):
    waitingForKey = State()

@r.message(Command("start"))
async def start(msg: types.Message, state: FSMContext):
    await msg.answer("Введите ключ доступа")
    await state.set_state(Register.waitingForKey)

@r.message(state=Register.waitingForKey)
async def addUser(message: types.Message, state: FSMContext):
    pass
