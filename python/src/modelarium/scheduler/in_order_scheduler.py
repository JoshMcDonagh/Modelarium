import jpype

from modelarium._java import java_class
from modelarium.scheduler.java_scheduler import JavaScheduler


class InOrderScheduler(JavaScheduler):
    def __init__(self):
        super().__init__(java_class("modelarium.scheduler.InOrderScheduler")())
