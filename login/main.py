import os
import sys
from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy import create_engine

from sanic import Sanic
from sanic.response import json


engine = create_engine('sqlite:///sqlalchemy_example.db')

Base = declarative_base()

class User(Base):
    __tablename__ = 'person'
    id = Column(Integer, primary_key=True)
    name = Column(String, nullable=False)
    password = Column(String(250), nullable=False)

Base.metadata.create_all(engine)
