import asyncio
import dbWorker
import logging
from aiogram import *

from cfg import TOKEN
from handlers import r
async def main():
    bot = Bot(TOKEN)
    dp = Dispatcher()
    dp.include_router(r)
    await bot.delete_webhook(drop_pending_updates=True)
    await dp.start_polling(bot, allowed_updates=dp.resolve_used_update_types())

if __name__=="__main__":
    logging.basicConfig(level=logging.INFO)
    asyncio.run(main())