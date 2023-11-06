import logging
import sqlite3
import mysql.connector
from mysql.connector import Error
import time
import keyGen
import json
from cfg import DB_PATH, DB_USER, DB_PASSWORD,DB_HOST
#сделать добавление задач
logging.basicConfig(level=logging.INFO)
class dataBaseWorker():
    #------------------DATABASE--------------------
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
            base = mysql.connector.connect(host=DB_HOST, user=DB_USER, password=DB_PASSWORD, database=self.dbPath)
            cur = base.cursor()
        except Error as e:
            logging.log(20,"Trying to connect to db again")
            print(e)
            time.sleep(5)
            self.connectBase()
        return base,cur


    def dropTables(self):
        base,cur  = self.connectBase()
        for i in ("Tasks", "Auth", "Users", "OneTimeKeys", "Subjects"):
            cur.execute("DROP TABLE %s", (i,))
        base.commit();cur.close();base.close()


    def createTables(self):
        base,cur = self.connectBase()
        cur.execute("""CREATE TABLE IF NOT EXISTS Auth 
                    (id INTEGER PRIMARY KEY AUTO_INCREMENT, 
                    user_name TEXT, 
                    tg_id INTEGER, 
                    password TEXT)""")
        cur.execute("CREATE TABLE IF NOT EXISTS Subjects (id INTEGER PRIMARY KEY AUTO_INCREMENT, subject_name TEXT, aliases JSON)")
        cur.execute("""CREATE TABLE IF NOT EXISTS Tasks 
                    (id INTEGER PRIMARY KEY AUTO_INCREMENT, 
                    subject_id INTEGER, 
                    description TEXT, 
                    from_date DATE,
                    to_date TEXT, 
                    is_redacting BOOLEAN,
                    FOREIGN KEY (subject_id) REFERENCES Subjects (id) ON DELETE CASCADE)""")
        cur.execute("""CREATE TABLE IF NOT EXISTS Users 
                    (id INTEGER PRIMARY KEY AUTO_INCREMENT, 
                    user_name TEXT, 
                    group_id TEXT, 
                    finished_tasks JSON, 
                    redacting_task INTEGER)""")
        cur.execute("CREATE TABLE IF NOT EXISTS OneTimeKeys (id INTEGER PRIMARY KEY AUTO_INCREMENT, key_value TEXT)")
        base.commit(); cur.close(); base.close()
        logging.log(20,"Tables created successfully")
#-----------------END------------------




#---------------USER-------------------
    def isUserNAME(self, userName: str): # -> boolean
        base, cur = self.connectBase()
        cur.execute("SELECT user_name FROM Auth")
        reply = True if userName in [i[0] for i in cur.fetchall()] else False
        cur.close(); base.close()
        logging.log(20,"Checked user for existance by userName")
        return reply


    def isUserTG(self, tg_id: int): # -> boolean
        base, cur = self.connectBase()
        cur.execute("SELECT tg_id FROM Auth")
        reply = True if tg_id in [i[0] for i in cur.fetchall()] else False
        cur.close(); base.close()
        logging.log(20, "Checked user for existance by tg_id")
        return reply


    def addUser(self, userName: str, tg_id: int, key: str, group_id: int):
        base, cur = self.connectBase()
        cur.execute("INSERT INTO Users (user_name, group_id, finished_tasks, redacting_task) VALUES (%s, %s, %s, %s)",(userName, group_id, json.dumps([]), -1))
        cur.execute("INSERT INTO Auth (user_name, tg_id, password) VALUES (%s, %s, %s)", (userName, tg_id, keyGen.getHash(key)))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new User")


    def writeOneTimeKeys(self, val =34):
        base, cur = self.connectBase()
        values = keyGen.generateKeys(val)
        print(values)

        for v in values:
            cur.execute("INSERT INTO OneTimeKeys (key_value) VALUES (%s)",(v,))
        cur.close(); base.commit(); base.close()
        logging.log(20,"OneTimeKeys WRITTEN TO DB SUCCESFULLY")


    def verifyOneTimeKey(self, key: str): # -> boolean
        h = keyGen.getHash(key)
        base, cur = self.connectBase()
        cur.execute("SELECT key_value FROM OneTimeKeys")
        l = [i[0] for i in cur.fetchall()] # -> list[str] - hashes
        cur.close(); base.close()
        # print(h,"\n",l)
        isValid = True if h in l else False
        logging.log(20,"Verified OneTimeKey")
        return isValid


    def checkPassword(self, userName, password: str):# -> boolean
        base, cur = self.connectBase()
        cur.execute("SELECT password FROM Auth WHERE user_name = %s", (userName,))
        isValid = keyGen.getHash(password) in [i[0] for i in cur.fetchall()]
        cur.close(); base.close()
        logging.log(20,"Password checked succesfully")
        return isValid


    def delOneTimeKey(self, key: str):
        base, cur = self.connectBase()
        cur.execute(f"DELETE FROM OneTimeKeys WHERE key_value=%s",(keyGen.getHash(key),))
        logging.log(20,"Deleted OneTimeKey")
        cur.close(); base.commit(); base.close()


    def addTelegramToExisting(self, tg_id: int, userName: str):
        base, cur = self.connectBase()
        cur.execute(f"UPDATE Auth SET tg_id = %s WHERE user_name = %s",(tg_id, userName))
        logging.log(20, "Added tg_id to existing user")
        cur.close(); base.commit(); base.close()
#-----------------------END-------------------------


#-----------------------TASKS & SUBJECTS------------------------
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
        cur.execute("INSERT INTO Tasks (subject_id, to_date, description) VALUES (%s, %s, %s)", (subID,date,description))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new task")


    def delTask(self, id):
        base, cur = self.connectBase()
        cur.execute("DELETE FROM Tasks WHERE id = %s",(id,))
        logging.log(20,"Deleted task")
        cur.close(); base.commit(); base.close()


    def markAsComplete(self, taskID: int, user_name: str):
        base, cur = self.connectBase()
        cur.execute("SELECT finished_tasks FROM Users WHERE user_name = %s", (user_name,))
        complete_tasks = json.loads(cur.fetchone()[0])+[taskID]
        cur.execute("UPDATE Users SET finished_tasks = %s WHERE user_name = %s", (json.dumps(complete_tasks),user_name))
        cur.close();base.commit();base.close()
        logging.log(20,"Marked task as complete")


    def addSub(self,name):
        base, cur = self.connectBase()
        name = name.lower()
        cur.execute("INSERT INTO Subjects (subject_name, aliases) VALUES (%s, %s)",(name,json.dumps([name])))
        cur.close(); base.commit(); base.close()
        logging.log(20,"New subject added")


    def delSubByID(self,ID):
        base, cur = self.connectBase()
        cur.execute("DELETE FROM Subjects WHERE id = %s", (ID,))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Subject deleted")


    def getSubjectsAliases(self): # -> list [str]
        base, cur = self.connectBase()
        cur.execute("SELECT aliases FROM Subjects")
        raw_subs = [json.loads(i[0]) for i in cur.fetchall()]
        print(raw_subs)
        subs = []
        for i in raw_subs:
            for a in i: subs.append(a)
        print(subs)
        cur.close(); base.commit(); base.close()
        logging.log(20,"Fetched all aliases from subject")
        return subs


    def getSubjectNamesAndIDs(self): # -> list [(int, str),]
        base, cur = self.connectBase()
        cur.execute("SELECT id, subject_name FROM Subjects")
        l = cur.fetchall()
        cur.close(); base.commit(); base.close()
        return l


    def getSubjectNamesAndAliases(self): # -> list [(str, json([str]))]
        base, cur = self.connectBase()
        cur.execute("SELECT subject_name, aliases FROM Subjects")
        subs = cur.fetchall()
        cur.close(); base.commit(); base.close()
        logging.log(20,"Fetched subject_name's")
        return subs


    def getSubjectIDsAndAliases(self): #-> list [[id,[aliases]]]
        base, cur = self.connectBase()
        cur.execute("SELECT id,aliases FROM Subjects")
        subs = cur.fetchall()
        print(subs)
        cur.close(); base.commit(); base.close()
        logging.log(20, "Fetched subject_name's & aliases")
        return subs


    def getTasks(self, user_name: str): # -> list [(int, str),]
        base, cur = self.connectBase()
        cur.execute("SELECT finished_tasks FROM Users WHERE user_name = %s", (user_name,))
        complete = json.loads(cur.fetchone()[0])
        cur.execute("SELECT id,description FROM Tasks")
        tasks = cur.fetchall()
        tasks = [i for i in tasks if i[0] not in complete]
        cur.close(); base.close()
        logging.log(20, "Fetched tasks")
        return tasks

    def updateTask(self, task_id: int, date = "", description = ""):
        base, cur = self.connectBase()
        if date!="": cur.execute("UPDATE Tasks SET to_date = %s WHERE id = %s",(date,task_id))
        else: cur.execute("UPDATE Tasks SET description = %s WHERE id = %s", (description,task_id))
        cur.close();base.commit();base.close()
        logging.log(20, "Updated Task")
        pass


    def getUncompletedTasks(self, user_name): # -> str
        base, cur = self.connectBase()
        l = ""
        cur.execute("SELECT id, subject_name FROM Subjects")
        for sub in cur.fetchall():
            print(sub[0])
            cur.execute("SELECT finished_tasks FROM Users WHERE user_name = %s", (user_name,))
            complete = json.loads(cur.fetchone()[0])
            cur.execute("SELECT subject_id, to_date, description, id FROM Tasks WHERE subject_id = %s", (sub[0],))
            tasks = cur.fetchall()
            print(tasks)
            print(complete)
            tasks = [i for i in tasks if i[3] not in complete]
            line = sub[1]+"\n"+"\n".join([i[1]+" - "+i[2] for i in tasks])+"\n"
            l+=line
        return l


    def addAlias(self, subject_name: str, alias: str):
        base, cur = self.connectBase()
        subject_name, alias = subject_name.lower(), alias.lower()
        cur.execute("SELECT aliases FROM Subjects WHERE subject_name = %s", (subject_name,))
        aliases = json.loads(cur.fetchone()[0])+[alias]
        cur.execute("UPDATE Subjects SET aliases = %s WHERE subject_name = %s", (json.dumps(aliases),subject_name))
        cur.close(); base.commit(); base.close()
        logging.log(20,"Added new Alias")


    def aliasIsValid(self, subject_name, alias): #-> 0,1,2 : 0-is valid, 1-subject_name not found, 2-alias already exists
        base, cur = self.connectBase()
        subject_name, alias = subject_name.lower(), alias.lower()
        cur.execute("SELECT aliases FROM Subjects")
        raw_aliases = [json.loads(i[0]) for i in cur.fetchall()]
        aliases = []
        for i in raw_aliases:aliases.append(i)
        cur.execute("SELECT subject_name FROM Subjects")
        if not subject_name in [i[0] for i in cur.fetchall()]:return 1
        elif alias in aliases:return 2
        return 0


    def getUsers(self): # -> list [int,]
        base, cur = self.connectBase()
        cur.execute("SELECT tg_id FROM Auth")
        data = [i[0] for i in cur.fetchall()]
        cur.close();base.close()
        return data


    def getUserNameByTGID(self, tgID: int): # -> str
        base, cur = self.connectBase()
        cur.execute("SELECT user_name FROM Auth WHERE tg_id = %s", (tgID,))
        userName = cur.fetchone()[0]
        cur.close();base.close()
        return userName


def getInput():
    return input("""Enter a number
                    0 - generate one time keys
                    1 - regenerate db
                    2 - get users
                    3 - exit\n
                    """)

if __name__ == "__main__":
    db = dataBaseWorker(DB_PATH)
    logging.basicConfig(level=0)
    while(True):
        i = getInput()
        if i == "0":
            db.writeOneTimeKeys(int(input("enter a number of keys")))
        elif i=="1":
            db.dropTables()
            db.createTables()
        elif i== "2":
            print(db.getUsers())
        elif i=="3":
            exit(0)
