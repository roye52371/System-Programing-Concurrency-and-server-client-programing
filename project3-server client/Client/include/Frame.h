//
// Created by royek on 10/01/2020.
//

#ifndef BOOST_ECHO_CLIENT_FRAME_H
#define BOOST_ECHO_CLIENT_FRAME_H

#include <string>
#include <map>
#include <atomic>

using namespace std;

class Frame {

public:
    Frame(string stompCommand, map<string,string> &headers, string bodyFrame);
    string getStompCommand();
    map<string, string>& getHeaders();
    string getFrameBody();
    static int getFrameId();

    static int frameId;

private:
    string stompCommand;
    map<string, string> headers;
    string frameBody;

};


#endif //BOOST_ECHO_CLIENT_FRAME_H
