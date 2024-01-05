DarkProxyManager je plugin zajišťující dynamickou tvorbu serverů na základě docker image templatů.

Základem se vytváří na Proxy lobby server, který se dynamicky rozšířuje a spravuje dle load balanceru.

Zbytek serverů se tvoří tzv. na poptávků. Tedy v moment kdy není aktivní server se jeden vytvoří, pokud je server přetížený vytvoří se nový. Poté máme 2 servery, které se aktivně balancují navzájem.
