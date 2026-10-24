from modelarium._java import java_class
from modelarium.scheduler.java_scheduler import JavaScheduler


class RandomOrderScheduler(JavaScheduler):
    def __init__(self):
        super().__init__(java_class("modelarium.scheduler.RandomOrderScheduler")())
