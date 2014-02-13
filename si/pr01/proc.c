/*
*
*MUEI - CF - Sistemas Inteligentes
*Practica 01
*Autor: W. Joel Castro Reynoso
*Email: wilton.castro@udc.es
*
*/
 
#include <stdio.h>

#define MAX_LETTERS 30
#define MAX_FILE_LENGTH 50000
#define MAX_STATEMENTS 1000
#define STATEMENT_LENGTH 10

int vocabulary[MAX_LETTERS];
int statements[MAX_STATEMENTS][STATEMENT_LENGTH];
char file[MAX_FILE_LENGTH];
char *current_statement, *next_statement;
char *current_letter;

void main()
{
	int readbytes = read(0, file, MAX_FILE_LENGTH)>0;
	if (readbytes)
	{
		printf("%s\n", file);
		current_statement = strtok(file, ".\n");
		int i =  0;
		while(current_statement!=NULL)
		{
			//next_statement = strtok(NULL, ".");
			i++;
			printf("%d - %s\n", i, current_statement);

			current_statement = strtok(NULL, ".\n");
		}
	}
	printf("\n");
}

