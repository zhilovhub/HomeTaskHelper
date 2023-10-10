import logging
import sqlite3
import time
import keyGen
import json
#сделать добавление задач
logging.basicConfig(level=logging.INFO)
class dataBaseWorker():
    def __init__(self,dbPath):
        self.dbPath = dbPath
        logging.log(20,"Checking tables")
        self.createTables()
        self.USERNAME = 1
        self.TG_ID = 0
        self.writeOneTimeKeys()
    def connectBase(self):
        try:
            logging.log(20,"Trying to connect to db")
            base = sqlite3.connect(self.dbPath)
            cur = base.cursor()
        except:
            logging.log(20,"Trying to connect to db again")
            time.sleep(5)
            self.connectBase()
        return base,cur
    def createTables(self):
        base,cur = self.connectBase()
        cur.execute("""CREATE TABLE IF NOT EXISTS Auth 
                    (num INTEGER PRIMARY KEY AUTOINCREMENT, 
                    user_name TEXT, 
                    tg_id INTEGER, 
                    password TEXT)""")
        cur.execute("""CREATE TABLE IF NOT EXISTS Tasks 
                    (num INTEGER PRIMARY KEY AUTOINCREMENT, 
                    title TEXT, 
                    desription TEXT, 
                    datetime DATE, 
                    is_redacting BOOLEAN)""")
        cur.execute("""CREATE TABLE IF NOT EXISTS Users 
                    (num INTEGER PRIMARY KEY AUTOINCREMENT, 
                    user_name TEXT, 
                    group_id TEXT, 
                    finished_tasks LIST, 
                    redacting_task INTEGER)""")
        cur.execute("CREATE TABLE IF NOT EXISTS OneTimeKeys (num INTEGER PRIMARY KEY AUTOINCREMENT, key_value TEXT)")
        base.commit(); cur.close(); base.close()
        logging.log(20,"Tables created successfully")
    def isUserNAME(self, userName: str): # -> boolean
        base, cur = self.connectBase()
        reply = True if userName in [i[0] for i in cur.execute("SELECT user_name FROM Auth").fetchall()] else False
        cur.close(); base.close()
        logging.log(20,"Checked user for existance by userName")
        return reply
    def isUserTG(self, tg_id:int): # -> boolean
        base, cur = self.connectBase()
        print([i[0] for i in cur.execute("SELECT tg_id FROM Auth").fetchall()])
        reply = True if tg_id in [i[0] for i in cur.execute("SELECT tg_id FROM Auth").fetchall()] else False
        cur.close(); base.close()
        logging.log(20, "Checked user for existance by tg_id")
        return reply
    def addUser(self, userName: str, tg_id: int, key: str, group_id: int):
        base, cur = self.connectBase()
        cur.execute("INSERT INTO Users (user_name, group_id, finished_tasks, redacting_task) VALUES (?, ?, ?, ?)",(userName, group_id, json.dumps([]), -1))
        cur.execute("INSERT INTO Auth (user_name, tg_id, password) VALUES (?, ?, ?)", (userName, tg_id, keyGen.getHash(key)))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new User")
    def addTask(self, title: str, description: str):
        base, cur = self.connectBase()
        cur.execute("INSERT INTO Tasks (title, description) VALUES (?, ?)", (title, description))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new task")
    def writeOneTimeKeys(self):
        base, cur = self.connectBase()
        values = keyGen.generateKeys(34)
        cur.executemany("INSERT INTO OneTimeKeys (key_value) VALUES (?)",zip(values))
        cur.close(); base.commit(); base.close()
        logging.log(20,"OneTimeKeys WRITTEN TO DB SUCCESFULLY")

    def verifyOneTimeKey(self, key: str): # -> boolean
        h = keyGen.getHash(key)
        base, cur = self.connectBase()
        l = [i[0] for i in cur.execute("SELECT key_value FROM OneTimeKeys").fetchall()] # -> list[str] - hashes
        cur.close(); base.close()
        # print(h,"\n",l)
        isValid = True if h in l else False
        logging.log(20,"Verified OneTimeKey")
        return isValid
    def checkPassword(self, userName, password: str):# -> boolean
        base, cur = self.connectBase()
        print([i[0] for i in cur.execute("SELECT password FROM Auth WHERE user_name = ?", (userName,)).fetchall()])
        isValid = True if keyGen.getHash(password) in [i[0] for i in cur.execute("SELECT password FROM Auth WHERE user_name = ?", (userName,)).fetchall()] else False
        cur.close(); base.close()
        return isValid
    def delOneTimeKey(self, key: str):
        base, cur = self.connectBase()
        cur.execute(f"DELETE FROM OneTimeKeys WHERE key_value=?",(keyGen.getHash(key),))
        logging.log(20,"Deleted OneTimeKey")
        cur.close(); base.commit(); base.close()

    def addTelegramToExisting(self, tg_id: int, userName: str):
        base, cur = self.connectBase()
        cur.execute(f"UPDATE Auth SET tg_id = ? WHERE user_name = ?",(tg_id, userName))
        logging.log(20, "Added tg_id to existing user")
        cur.close(); base.commit(); base.close()