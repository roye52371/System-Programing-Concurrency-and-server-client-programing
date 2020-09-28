//
// Created by sagiv on 1/13/20.
//

#include "IOKeyboard.h"

IOKeyboard::IOKeyboard(ConnectionHandler &connectionHandler): connectionHandler(connectionHandler) {}

void IOKeyboard::run() {
    const short bufsize = 1024;
    char buf[bufsize];
    while(!(connectionHandler.isShouldTerminate())) {
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        this->connectionHandler.sendLineKeyboard(line);
        while(connectionHandler.isError() && line.compare("bye")!=0){
            std::cout<<"you did not entered the word bye, please enter it now"<<endl;
            std::cin.getline(buf, bufsize);
            std::string s(buf);
            line=s;
        }
    }
}