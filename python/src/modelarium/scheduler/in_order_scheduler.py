from modelarium.scheduler.java_scheduler import JavaScheduler


class InOrderScheduler(JavaScheduler):
    def __init__(self):
        from modelarium._java import java_class
        super().__init__(java_class("modelarium.scheduler.InOrderScheduler")())
