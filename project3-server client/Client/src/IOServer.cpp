//
// Created by sagiv on 1/14/20.
//

#include "IOServer.h"

IOServer::IOServer(ConnectionHandler &connectionHandler): connectionHandler(connectionHandler) {}

void IOServer::run() {
    while(!(connectionHandler.isTerminate())) {
        string serverAnswer;
        this->connectionHandler.getLine(serverAnswer);
        printFrame(serverAnswer);
        this->connectionHandler.sendLineServer(serverAnswer);
    }
}

void IOServer::printFrame(string frame) {
    cout<<""<<endl;
    frame = frame.substr(0, frame.size() - 1);
    frame += "^@";
    cout<<frame<<endl;
}
