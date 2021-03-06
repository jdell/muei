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
#define MAX_ITERATIONS 10

int vocabulary[MAX_LETTERS];
int statements[MAX_STATEMENTS][STATEMENT_LENGTH];
char file[MAX_FILE_LENGTH];
char *current_statement;
char *current_letter;
int index_letter, index_statement;

char *delimiters = "<-,.";
char statement_delimiter = '\n';

int i, j;

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
	//Reading
	int readbytes = read(0, file, MAX_FILE_LENGTH)>0;
	if (readbytes)
	{
		//Parsing
		char *p = file;
		index_statement = 0;
 		while (p) {
        		current_statement = scan(&p, statement_delimiter);
			if (strcmp(current_statement, "")==0) break;

			printf("%d - %s\n", index_statement, current_statement);
			index_letter = 0;
			for(current_letter = strtok(current_statement, delimiters); current_letter!=NULL; current_letter = strtok(NULL, delimiters))
			{
				statements[index_statement][index_letter] = *current_letter - 'a' + 1;
				index_letter++;

				printf("\tletter: %s\n", current_letter);
			}
			index_statement++;
		}

		int iteration = 0;
		for (iteration = 0; iteration<MAX_ITERATIONS; iteration++)
		{
			//Execution
			int res;
			for (i=0; i<index_statement; i++)
			{
				res = 1;
				for (j=1; j<STATEMENT_LENGTH; j++)
				{
					index_letter = statements[i][j];
					if  (index_letter==0) break;
					if (vocabulary[index_letter]!=1)
					{
						res = 0;
						break;
					}
				}
				index_letter = statements[i][0];
				vocabulary[index_letter] = res;

				//for(j=1; j<MAX_LETTERS; j++)
				//	printf("%d", vocabulary[j]);
				//printf("\n");
			}

			//Print
			for (i=1; i<MAX_LETTERS; i++)
		   		printf("%d", vocabulary[i]);

			printf("\n");
		}
	}
	printf("\n");
}
