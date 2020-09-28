#include <stdlib.h>
#include <connectionHandler.h>
#include <thread>
#include "IOKeyboard.h"
#include "IOServer.h"
#include "Client.h"

using namespace std;

void split(const string &txt, vector<string> &strs) {
    size_t pos = txt.find(" ");
    size_t initialPos = 0;
    strs.clear();
    while( pos != std::string::npos ) {
        strs.push_back( txt.substr( initialPos, pos - initialPos ) );
        initialPos = pos + 1;
        pos = txt.find(" ", initialPos );
    }
    strs.push_back( txt.substr( initialPos, std::min( pos, txt.size() ) - initialPos + 1 ) );
}

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/
int main (int argc, char *argv[]) {

    const short bufsize = 1024;
    char buf[bufsize];
    std::cin.getline(buf, bufsize);
    std::string line(buf);
    vector<string> strs;
    split(line, strs);
    while (!(strs.size() == 4 && strs.at(0) == "login")) {
        std::cin.getline(buf, bufsize);
        std::string s(buf);
        line = s;
        strs.clear();
        split(line,strs);
    }
    int pos = strs.at(1).find(":");
    string host = strs.at(1).substr(0, pos);
    short port = stoi(strs.at(1).substr(pos + 1));
    string clientName = strs.at(2);
    string pass = strs.at(3);
    Client client(clientName);
    mutex mutex;
    MessagingProtocolSTOMP messagingProtocolStomp(client);
    ConnectionHandler connectionHandler(host, port, messagingProtocolStomp,mutex);
    if (!connectionHandler.connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    connectionHandler.sendLineKeyboard(line);

    IOKeyboard task1(connectionHandler);
    IOServer task2(connectionHandler);

    thread thread1(&IOKeyboard::run, &task1);
    thread thread2(&IOServer::run, &task2);

    thread1.join();
    thread2.join();

    return 0;

}