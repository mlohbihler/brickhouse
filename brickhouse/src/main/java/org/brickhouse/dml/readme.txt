Test
----
a and (b="asdf" or c) fields x,y sort x
a.b.name="parent" fields a.b.name,x,y sort x
id="" set a=true sort x

Functional
----------
and(a, or(eq(b, "asdf"), c)).fields(x, y).sort(x)
eq(a.b.name, "parent").fields(a.b.name, x, y).sort(x)
eq(id, "").set(a, true).sort(x)
