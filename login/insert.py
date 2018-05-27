from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from main import Base, User
engine = create_engine('sqlite:///sqlalchemy_example.db')

Base.metadata.bind = engine

DBSession = sessionmaker(bind=engine)
session= DBSession()

new_person = User(name='guy', password='test')

session.add(new_person)
session.commit()
