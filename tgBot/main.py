from cfg import TOKEN,WEBHOOK_PATH,WEBHOOK_SECRET,WEB_SERVER_HOST,WEB_SERVER_PORT,BASE_WEBHOOK_URL,SSL_PEM,SSL_KEY
from handlers import r
import dbWorker
import asyncio
import platform
from aiohttp import web
import logging
import ssl
from aiogram import Bot, Dispatcher
from aiogram.webhook.aiohttp_server import *
from aiogram.fsm.storage.memory import MemoryStorage
from aiogram.types import FSInputFile


async def dropWebhook(bot):
    await bot.delete_webhook(drop_pending_updates=True)


async def makeWebhook(bot):
    await dropWebhook(bot)
    await bot.set_webhook(f"{BASE_WEBHOOK_URL}{WEBHOOK_PATH}",certificate=FSInputFile(SSL_PEM),secret_token=WEBHOOK_SECRET)


def setupWebApp(bot,dp):
    dp.startup.register(makeWebhook)
    app = web.Application()
    webhookRequestHandler = SimpleRequestHandler(bot,dp,WEBHOOK_SECRET)
    webhookRequestHandler.register(app, path=WEBHOOK_PATH)
    setup_application(app, dp, bot=bot)
    context = ssl.SSLContext(ssl.PROTOCOL_TLSv1_2)
    context.load_cert_chain(SSL_PEM, SSL_KEY)
    web.run_app(app, host=WEB_SERVER_HOST, port=WEB_SERVER_PORT,ssl_context=context)


def setupBot():
    bot = Bot(TOKEN)
    dp = Dispatcher(storage=MemoryStorage())
    dp.include_router(r)
    return bot,dp


async def main():
    bot,dp = setupBot()
    if platform.system() in ["Darwin", "Windows"]:
        await startBot(bot,dp)
    else:
        setupWebApp(bot,dp)


async def startBot(bot,dp):
    await dropWebhook(bot)
    await dp.start_polling(bot, allowed_updates=dp.resolve_used_update_types())


if __name__=="__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(main())