# Linked Data Crawler

1. Fetch Raw [LDResponse(redirects,headers,body) 
2. Parse
3. Identify 


RUN
```
mvn spring-boot:run -Dstart-class=org.dbpedia.ld.crawl.fetch.FetcherApp
```


```
mvn spring-boot:run -Dstart-class=org.dbpedia.ld.crawl.fetch.FetcherApp -Dspring-boot.run.arguments="../data/test/in.lst.min10"
```

# Backlog
- [ ] test rocksdb impl with DBpedia
- [ ] remove ':' from queue name
- [ ] add dummy query trait
- [x] remove println from datastore
- [x] add groupByStatuscode request
