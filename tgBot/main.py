from cfg import TOKEN,WEBHOOK_PATH,WEBHOOK_SECRET,WEB_SERVER_HOST,WEB_SERVER_PORT,BASE_WEBHOOK_URL
from handlers import r
import dbWorker
import asyncio
import platform
from aiohttp import web
import logging
from aiogram import Bot, Dispatcher
from aiogram.webhook.aiohttp_server import *
from aiogram.fsm.storage.memory import MemoryStorage


async def dropWebhook(bot):
    await bot.delete_webhook(drop_pending_updates=True)


async def makeWebhook(bot):
    await dropWebhook(bot)
    await bot.set_webhook(f"{BASE_WEBHOOK_URL}{WEBHOOK_PATH}", secret_token=WEBHOOK_SECRET)


def setupWebApp(bot,dp):
    dp.startup.register(makeWebhook)
    app = web.Application()
    webhookRequestHandler = SimpleRequestHandler(bot,dp)
    webhookRequestHandler.register(app, path=WEBHOOK_PATH)
    setup_application(app, dp, bot=bot)
    web.run_app(app, host=WEB_SERVER_HOST, port=WEB_SERVER_PORT)


def setupBot():
    bot = Bot(TOKEN)
    dp = Dispatcher(storage=MemoryStorage())
    dp.include_router(r)
    return bot,dp


async def main():
    await startBot(*setupBot())


async def startBot(bot,dp):
    if platform.system() in ["Darwin","Windows"]:
        await dropWebhook(bot)
        await dp.start_polling(bot, allowed_updates=dp.resolve_used_update_types())
    else:
        setupWebApp(bot,dp)


if __name__=="__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(main())