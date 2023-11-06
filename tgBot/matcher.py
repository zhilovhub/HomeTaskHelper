import re
from dbWorker import dataBaseWorker
def isTaskValid(line: str, worker: dataBaseWorker): # -> boolean
    return re.fullmatch("добавь .+ на ((0\d)|(1\d)|(2\d)|(3[01]))\.((0\d)|(1[012])) .*", line.lower())


def isSubjectExists(subject_name:str, worker:dataBaseWorker): #-> boolean
    return subject_name.lower() in worker.getSubjectsAliases()


def prepareTask(line: str): # -> list[str]
    line = line.lower()
    # print(re.search("(?<=добавь ).*(?= на)",line))
    subject = re.search("(?<=добавь ).*(?= на)",line).group().capitalize()
    date = re.search("\d{2}\.\d{2}",line).group()
    description = re.search("(?<=\d{2}\.\d{2} ).*",line).group().capitalize()
    return subject,date,description


def prepareAlias(line:str): # -> list[str]
    line = line.lower()
    subject = re.search(".+(?= *,)",line).group()
    alias = re.search("(?<=,).+",line).group().strip()
    return [subject,alias]


def isAliasStringValid(line:str): # -> boolean
    return re.fullmatch(".+,.+",line)


def prepareSub(line:str):
    line = line.lower()
    subject = re.search("(?<=/newsub ).+",line).group()
    return subject


def isUserNameValid(line:str): # -> boolean
    return re.fullmatch(r"[\w\d]{4,64}",line)


def isPasswordValid(line:str): # -> boolean
    return re.fullmatch(r".{8,1024}", line)

def isDateValid(line:str): # -> boolean
    return re.fullmatch(r"((0\d)|(1\d)|(2\d)|(3[01]))\.((0\d)|(1[012]))", line)