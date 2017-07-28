#include <string.h>
#include <stdlib.h>
#include <assert.h>
#include <stdio.h>
#include <limits.h>
typedef int int16_t;
#define ARRAY(T) struct {\
    int16_t size;\
    int16_t capacity;\
    T *data;\
} *
#define ARRAY_CREATE(array, init_capacity, init_size) {\
    array = malloc(sizeof(*array)); \
    array->data = malloc((init_capacity) * sizeof(*array->data)); \
    assert(array->data != NULL); \
    array->capacity = init_capacity; \
    array->size = init_size; \
}
#define ARRAY_PUSH(array, item) {\
    if (array->size == array->capacity) {  \
        array->capacity *= 2;  \
        array->data = realloc(array->data, array->capacity * sizeof(*array->data)); \
        assert(array->data != NULL); \
    }  \
    array->data[array->size++] = item; \
}
#define STR_INT16_T_BUFLEN ((CHAR_BIT * sizeof(int16_t) - 1) / 3 + 2)
void str_int16_t_cat(char *str, int16_t num) {
    char numstr[STR_INT16_T_BUFLEN];
    sprintf(numstr, "%d", num);
    strcat(str, numstr);
}
int16_t gc_i;

static ARRAY(void *) gc_main;
char * tmp_string = NULL;
static int16_t i;
int main(void) {
    ARRAY_CREATE(gc_main, 2, 0);

    i = 10;
    for (;i > 0;i--)
    {
        tmp_string = malloc(strlen("Printing ") + STR_INT16_T_BUFLEN + 1);
        assert(tmp_string != NULL);
        tmp_string[0] = '\0';
        strcat(tmp_string, "Printing ");
        str_int16_t_cat(tmp_string, i);
        ARRAY_PUSH(gc_main, tmp_string);
        printf("%s\n", tmp_string);
    }
    for (gc_i = 0; gc_i < gc_main->size; gc_i++)
        free(gc_main->data[gc_i]);
    free(gc_main->data);
    free(gc_main);

    return 0;
}
