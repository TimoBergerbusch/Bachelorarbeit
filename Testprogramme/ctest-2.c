/* doesn't terminate */
/* gives us a rule f_a(x,y)->f_a(z,y) :|: ... && b*x+c = d*z+e */

int main(){
    int a=2, b =1;

    while(a+b<10){
        a=a+b;
        b=b-1;
    }
}
