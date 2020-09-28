
CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude -pthread
LFLAGS:=-lboost_system -pthread

all: StompBookClubClient

StompBookClubClient: bin/connectionHandler.o bin/echoClient.o bin/Book.o bin/Client.o bin/Frame.o bin/IOKeyboard.o bin/IOServer.o bin/MessagingProtocolSTOMP.o
	g++ -o bin/StompBookClubClient bin/connectionHandler.o bin/echoClient.o bin/Book.o bin/Client.o bin/Frame.o bin/IOKeyboard.o bin/IOServer.o bin/MessagingProtocolSTOMP.o $(LFLAGS)

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/Book.o: src/Book.cpp
	g++ $(CFLAGS) -o bin/Book.o src/Book.cpp

bin/Client.o: src/Client.cpp
	g++ $(CFLAGS) -o bin/Client.o src/Client.cpp

bin/Frame.o: src/Frame.cpp
	g++ $(CFLAGS) -o bin/Frame.o src/Frame.cpp

bin/IOKeyboard.o: src/IOKeyboard.cpp
	g++ $(CFLAGS) -o bin/IOKeyboard.o src/IOKeyboard.cpp

bin/IOServer.o: src/IOServer.cpp
	g++ $(CFLAGS) -o bin/IOServer.o src/IOServer.cpp

bin/MessagingProtocolSTOMP.o: src/MessagingProtocolSTOMP.cpp
	g++ $(CFLAGS) -o bin/MessagingProtocolSTOMP.o src/MessagingProtocolSTOMP.cpp

.PHONY: clean
clean:
	rm -f bin/*

memleak: all
	valgrind --leak-check=full --show-reachable=yes bin/StompBookClubClient
