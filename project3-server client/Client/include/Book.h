//
// Created by royek on 11/01/2020.
//

#ifndef BOOST_ECHO_CLIENT_BOOK_H
#define BOOST_ECHO_CLIENT_BOOK_H

#include <map>
#include <vector>
#include <stack>
#include <Client.h>

using namespace std;


class Book {

private:
  string BookName;
  string topic;
  string bookLender;

public:
    Book(string bookName, string bookCategory, string bookLender);
    string getBookLender();
    string getBookName() const;
    string getBookCategory();

};


#endif //BOOST_ECHO_CLIENT_BOOK_H
