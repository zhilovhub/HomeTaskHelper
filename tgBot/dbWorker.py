import logging
import sqlite3
import time
import keyGen

logging.basicConfig(level=logging.INFO)
class dataBaseWorker():
    def __init__(self,dbPath):
        self.dbPath = dbPath
        logging.log(20,"Checking tables")
        self.createTables()
        # self.writeOneTimeKeys()
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
                    tg_id TEXT, 
                    key INTEGER)""")
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
        base.commit()
        cur.close()
        base.close()
        logging.log(20,"Tables created successfully")
    def isUserExists(self, userName): # -> boolean
        base, cur = self.connectBase()
        reply = True if userName in [i[0] for i in cur.execute("SELECT user_name FROM Auth").fetchall()] else False
        cur.close()
        base.close()
        logging.log(20,"Checked user for existance")
        return reply
    def addUser(self, userName, tg_id, key, group_id):
        base, cur = self.connectBase()
        cur.execute("INSERT INTO Users (user_name, group_id) VALUES (?, ?)",(userName, group_id, [], -1))
        cur.execute("INSERT INTO Auth (user_name, tg_id, key) VALUES (?, ?, ?)", (userName, tg_id, keyGen.getHash(key)))
        cur.close()
        base.commit()
        base.close()
        logging.log(20,"Added new User")
    def addTask(self, title, description):
        base, cur = self.connectBase()
        cur.execute("INSERT INTO Tasks (title, description) VALUES (?, ?)", (title, description))
        cur.close()
        base.commit()
        base.close()
        logging.log(20,"Added new task")
    def writeOneTimeKeys(self):
        base, cur = self.connectBase()
        values = keyGen.generateKeys(34)
        cur.executemany("INSERT INTO OneTimeKeys (key_value) VALUES (?)",zip(values))
        cur.close()
        base.commit()
        base.close()
        logging.log(20,"OneTimeKeys WRITTEN TO DB SUCCESFULLY")

    def verifyOneTimeKey(self, key): # -> boolean
        h = keyGen.getHash(key)
        base, cur = self.connectBase()
        l = [i[0] for i in cur.execute("SELECT key_value FROM OneTimeKeys").fetchall()] # -> list[str] - hashes
        cur.close()
        base.close()
        print(h,"\n",l)
        isValid = True if h in l else False
        logging.log(20,"Verified OneTimeKey")
        return isValid
    def delOneTimeKey(self, key):
        base, cur = self.connectBase()
        cur.execute("DELETE FROM OneTimeKeys WHERE key_value=?",(key,))
        logging.log(20,"Deleted OneTimeKey")
        cur.close()
        base.commit()
        base.close()