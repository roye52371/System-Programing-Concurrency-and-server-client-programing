//
// Created by royek on 11/01/2020.
//

#include "Book.h"

using namespace std;


Book::Book(string bookName, string bookCategory, string bookLender): BookName(bookName), topic(bookCategory), bookLender(bookLender) {}

string Book::getBookLender() { return this->bookLender; }
string Book::getBookName() const { return  this->BookName; }
string Book::getBookCategory() { return this->topic; }


