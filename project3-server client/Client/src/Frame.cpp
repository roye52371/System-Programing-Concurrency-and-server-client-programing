//
// Created by royek on 10/01/2020.
//

#include "Frame.h"

int Frame::frameId(0);

Frame::Frame(string stompCommand, map<string, string> &headers, string bodyFrame): stompCommand(stompCommand), headers(headers), frameBody(bodyFrame) {}

string Frame::getStompCommand() { return this->stompCommand; }
map<string, string>& Frame::getHeaders() { return  this->headers; }
string Frame::getFrameBody(){ return this->frameBody; }
int Frame::getFrameId() { return frameId++; }



