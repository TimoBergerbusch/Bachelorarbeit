
int main(void)
{
    int a = __VERIFIER_nondet_int();
    int b = __VERIFIER_nondet_int();
    int c = __VERIFIER_nondet_int();
    while (a+b+c >= 4) {
        a = b;
        b = a+b;
        c = b-1;
    }
    return 0;
}

