import re
from dbWorker import dataBaseWorker
def taskIsValid(line: str, worker: dataBaseWorker): # -> boolean
    worker.getSubjectsAliases()
    line = line.lower()
    return True if re.fullmatch("добавь .+ на \d{2}.\d{2} .*",line) else False
def prepareTask(line: str): # -> list[str]
    line = line.lower()
    # print(re.search("(?<=добавь ).*(?= на)",line))
    subject = re.search("(?<=добавь ).*(?= на)",line).group().capitalize()
    date = re.search("\d{2}\.\d{2}",line).group()
    description = re.search("(?<=\d{2}\.\d{2} ).*",line).group().capitalize()
    return [subject,date,description]