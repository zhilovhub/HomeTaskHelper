import logging
import sqlite3
import time


class _DataBase():
    def __init__(self,dbPath):
        base = self.connectBase(dbPath)
    def connectBase(self,dbPath):
        try:
            base = sqlite3.connect(dbPath)
        except:
            time.sleep(50)
            self.connectBase(dbPath)
        return base

class MainDB(_DataBase):
    pass

class AuthDB(_DataBase):
    pass