package com.flipkart.adq.utils;

public class Tuple<A, B>
{
  public A a;
  public B b;

  public Tuple(A a, B b)
  {
    this.a = a;
    this.b = b;
  }

  public static <A, B> Tuple<A, B> create(A a, B b) {
    return new Tuple(a, b);
  }

  public A getA()
  {
    return this.a;
  }
  public B getB() { return this.b; }

}