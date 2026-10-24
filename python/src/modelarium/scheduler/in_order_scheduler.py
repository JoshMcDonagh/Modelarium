import jpype

from modelarium.scheduler.java_scheduler import JavaScheduler


class InOrderScheduler(JavaScheduler):
    def __init__(self):
        super().__init__(jpype.JClass("modelarium.scheduler.InOrderScheduler")())
