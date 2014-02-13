/*
*
*MUEI - CF - Sistemas Inteligentes
*Practica 01
*Autor: W. Joel Castro Reynoso
*Email: wilton.castro@udc.es
*
*/
 

#include <stdio.h>
#include <string.h>

#define MAX_LETTERS 30
#define MAX_FILE_LENGTH 50000
#define MAX_STATEMENTS 1000
#define STATEMENT_LENGTH 10

int vocabulary[MAX_LETTERS];
int statements[MAX_STATEMENTS][STATEMENT_LENGTH];
char file[MAX_FILE_LENGTH];
char *current_statement;
char *current_letter;

char *delimiters = "<-,.";
char statement_delimiter = '\n';

char *scan(char **pp, char c)
{
    char *s = *pp, *p;

    p = strchr(*pp, c);
    if (p) *p++ = '\0';
    *pp = p;
    return s;
}

void main()
{
	int readbytes = read(0, file, MAX_FILE_LENGTH)>0;
	if (readbytes)
	{
		char *p = file;
		int i = 0;
 		while (p) {
        		current_statement = scan(&p, statement_delimiter);
			if (strcmp(current_statement, "")==0) return;

			i++;
			printf("%d - %s\n", i, current_statement);
			current_letter = strtok(current_statement, delimiters);
			while(current_letter!=NULL)
			{
				printf("\tletter: %s\n", current_letter);
				current_letter = strtok(NULL, delimiters);
			}
		}
	}
	printf("\n");
}
