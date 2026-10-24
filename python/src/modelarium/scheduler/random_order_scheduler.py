import jpype

from modelarium.scheduler.java_scheduler import JavaScheduler


class RandomOrderScheduler(JavaScheduler):
    def __init__(self):
        super().__init__(jpype.JClass("modelarium.scheduler.RandomOrderScheduler")())
