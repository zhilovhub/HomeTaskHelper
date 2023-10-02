import logging
import sqlite3
import time

class dataBaseWorker():
    def __init__(self,dbPath):
        dbPath = dbPath
    def connectBase(self):
        try:
            base = sqlite3.connect(self.dbPath)
            cur = base.cursor()
        except:
            time.sleep(50)
            self.connectBase()
        return base,cur
    def createTables(self):
        base,cur = self.connectBase()
        cur.execute("CREATE TABLE Auth IF NOT EXISTS "
                    "(PRIMARY KEY INT AUTOINCREMENT, "
                    "TEXT name, "
                    "INT key)")
        cur.execute("CREATE TABLE Tasks IF NOT EXISTS "
                    "(PRIMARY KEY INT AUTOINCREMENT, "
                    "TEXT title, "
                    "TEXT desription, "
                    "BOOLEAN is_redacting")
        cur.execute("CREATE TABLE Users IF NOT EXISTS "
                    "(PRIMARY KEY INT AUTOINCREMENT, "
                    "TEXT tg_id, "
                    "TEXT group_id, "
                    "LIST finished_tasks, "
                    "INT redacting_task")
        cur.close()
        logging.log("Tables created successfully")
    def isUserExists(self, userID):
        base,cur = self.connectBase()
        reply = True if userID in cur.execute("SELECT * FROM Users").fetchall() else False
        cur.close()
        return reply
    def addUser(self, userID, key, group_id):
        cur = self.connectBase()[1]
        cur.execute("INSERT INTO Users (tg_id, group_id) VALUES (?, ?)",(userID, group_id, [], -1))
        cur.execute("INSERT INTO Auth VALUES (?, ?)", (userID, key))
        cur.close()
    def addTask(self, title, description):
        cur = self.connectBase()[1]
        cur.execute("INSERT INTO Tasks VALUES (?, ?)", (title, description))
        cur.close()