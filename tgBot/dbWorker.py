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
                    (id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    user_name TEXT, 
                    tg_id INTEGER, 
                    password TEXT)""")
        cur.execute("CREATE TABLE IF NOT EXISTS Subjects (id INTEGER PRIMARY KEY AUTOINCREMENT, subject_name TEXT, aliases LIST)")
        # cur.execute("DROP TABLE Tasks")
        cur.execute("""CREATE TABLE IF NOT EXISTS Tasks 
                    (id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    subject_id INTEGER, 
                    description TEXT, 
                    from_date DATE,
                    to_date TEXT, 
                    is_redacting BOOLEAN,
                    FOREIGN KEY (subject_id) REFERENCES Subjects (id) ON DELETE CASCADE)""")
        cur.execute("""CREATE TABLE IF NOT EXISTS Users 
                    (id INTEGER PRIMARY KEY AUTOINCREMENT, 
                    user_name TEXT, 
                    group_id TEXT, 
                    finished_tasks LIST, 
                    redacting_task INTEGER)""")
        cur.execute("CREATE TABLE IF NOT EXISTS OneTimeKeys (id INTEGER PRIMARY KEY AUTOINCREMENT, key_value TEXT)")
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
        # print([i[0] for i in cur.execute("SELECT tg_id FROM Auth").fetchall()])
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


    def addTask(self, subject_name: str, date: str, description: str):
        base, cur = self.connectBase()
        aliases = self.getSubjectIDsAndAliases()
        # print(aliases)
        # print(data[0])
        for i in aliases:
            print(json.loads(i[1]))
            if subject_name in json.loads(i[1]):subject_name=i[0]
            subID = i[0]
            print(subID)
        cur.execute("INSERT INTO Tasks (subject_id, to_date, description) VALUES (?, ?, ?)", (subID,date,description))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new task")


    def delTask(self, id):
        base, cur = self.connectBase()
        cur.execute("DELETE FROM Tasks WHERE id = ?",(id,))


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
        # print([i[0] for i in cur.execute("SELECT password FROM Auth WHERE user_name = ?", (userName,)).fetchall()])
        isValid = True if keyGen.getHash(password) in [i[0] for i in cur.execute("SELECT password FROM Auth WHERE user_name = ?", (userName,)).fetchall()] else False
        cur.close(); base.close()
        logging.log(20,"Password checked succesfully")
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


    def addSub(self,name):
        base, cur = self.connectBase()
        name = name.lower()
        cur.execute("INSERT INTO Subjects (subject_name, aliases) VALUES (?, ?)",(name,json.dumps([name])))
        # print(cur.execute("SELECT subject_name FROM Subjects").fetchall())
        cur.close(); base.commit(); base.close()
        logging.log(20,"New subject added")


    def delSubByID(self,ID):
        base, cur = self.connectBase()
        cur.execute("DELETE FROM Subjects WHERE id = ?", (ID,))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Subject deleted")


    def getSubjectsAliases(self):
        base, cur = self.connectBase()
        raw_subs = [json.loads(i[0]) for i in cur.execute("SELECT aliases FROM Subjects").fetchall()]
        print(raw_subs)
        subs = []
        for i in raw_subs:
            for a in i: subs.append(a)
        print(subs)
        cur.close(); base.commit(); base.close()
        logging.log(20,"Fetched all aliases from subject")
        return subs


    def getSubjectNamesAndIDs(self):
        base, cur = self.connectBase()
        l = cur.execute("SELECT id, subject_name FROM Subjects").fetchall()
        cur.close(); base.commit(); base.close()
        return l

    def getSubjectNames(self):
        base, cur = self.connectBase()
        subs = [i[0] for i in cur.execute("SELECT subject_name FROM Subjects").fetchall()]
        cur.close(); base.commit(); base.close()
        logging.log(20,"Fetched subject_name's")
        return subs


    def getSubjectIDsAndAliases(self): #-> [[id,[aliases]]]
        base, cur = self.connectBase()
        subs = cur.execute("SELECT id,aliases FROM Subjects").fetchall()
        print(subs)
        cur.close(); base.commit(); base.close()
        logging.log(20, "Fetched subject_name's & aliases")
        return subs


    def getTasks(self):
        base, cur = self.connectBase()
        tasks = cur.execute("SELECT id,description FROM Tasks").fetchall()
        cur.close(); base.commit(); base.close()
        logging.log(20, "Fetched tasks")
        return tasks


    def getAllTasksWithSubjects(self):
        base, cur = self.connectBase()
        l = ""
        for sub in cur.execute("SELECT id, subject_name FROM Subjects").fetchall():
            print(sub[0])
            tasks = cur.execute("SELECT subject_id, to_date, description FROM Tasks WHERE subject_id = ?", (sub[0],)).fetchall()
            print(cur.execute("SELECT subject_id, to_date, description FROM Tasks").fetchall())
            line = sub[1]+"\n"+"\n".join([i[1]+" - "+i[2] for i in tasks])+"\n"
            l+=line
        return l


    def addAlias(self, subject_name: str, alias: str):
        base, cur = self.connectBase()
        subject_name, alias = subject_name.lower(), alias.lower()

        # print(json.loads(cur.execute("SELECT aliases FROM Subjects WHERE subject_name = ?", (subject_name,)).fetchone()[0]))
        aliases = json.loads(cur.execute("SELECT aliases FROM Subjects WHERE subject_name = ?", (subject_name,)).fetchone()[0])+[alias]
        cur.execute("UPDATE Subjects SET aliases = ? WHERE subject_name = ?", (json.dumps(aliases),subject_name))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new Alias")


    def aliasIsValid(self, subject_name, alias): #-> 0,1,2 : 0-is valid, 1-subject_name not found, 2-alias already exists
        base, cur = self.connectBase()
        subject_name, alias = subject_name.lower(), alias.lower()
        raw_aliases = [json.loads(i[0]) for i in cur.execute("SELECT aliases FROM Subjects").fetchall()]
        # print(cur.execute("SELECT aliases FROM Subjects").fetchall())
        aliases = []
        for i in raw_aliases:aliases.append(i)
        # print(aliases)
        if not subject_name in [i[0] for i in cur.execute("SELECT subject_name FROM Subjects").fetchall()]:return 1
        elif alias in aliases:return 2
        return 0
