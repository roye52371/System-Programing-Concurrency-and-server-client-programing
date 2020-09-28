//
// Created by sagiv on 1/12/20.
//

#ifndef BOOST_ECHO_CLIENT_MESSAGINGPROTOCOLSTOMP_H
#define BOOST_ECHO_CLIENT_MESSAGINGPROTOCOLSTOMP_H

#include "Client.h"
#include "Frame.h"

class Frame;
class Client;

class MessagingProtocolSTOMP {

public:
    MessagingProtocolSTOMP(Client &client);
    string processFromKeyboard(string keyboardLine);
    string processFromServer(Frame &message);
    bool getShouldTerminate();
    bool getTerminate();
    bool getError();

private:
    Client client;
    bool shouldTerminate = false;
    bool terminate = false;
    bool error=false;
    map<int, string> reciptsInfo;

    void split(const string &txt, vector<string> &strs);

};


#endif //BOOST_ECHO_CLIENT_MESSAGINGPROTOCOLSTOMP_H
