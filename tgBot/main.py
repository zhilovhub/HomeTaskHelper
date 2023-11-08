from cfg import TOKEN,WEBHOOK_PATH,WEBHOOK_SECRET,WEB_SERVER_HOST,WEB_SERVER_PORT
from handlers import r as router
import asyncio
import platform
from aiohttp import web
import logging
from aiogram import Bot, Dispatcher
from aiogram.webhook.aiohttp_server import *
from aiogram.fsm.storage.memory import MemoryStorage
from aiogram.enums import ParseMode
import sys

def main() -> None:
    dp = Dispatcher(storage=MemoryStorage())
    dp.include_router(router)
    bot = Bot(TOKEN, parse_mode=ParseMode.HTML)
    app = web.Application()
    webhook_requests_handler = SimpleRequestHandler(
        dispatcher=dp,
        bot=bot,
        secret_token=WEBHOOK_SECRET,
    )
    webhook_requests_handler.register(app, path=WEBHOOK_PATH)
    setup_application(app, dp, bot=bot)
    web.run_app(app, host=WEB_SERVER_HOST, port=WEB_SERVER_PORT)


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO, stream=sys.stdout)
    main()