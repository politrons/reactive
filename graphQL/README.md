# Netflix Architecture


## Query

```graphql
query {
  movieById(id: "2") {
    id
    name
    duration
    director {
      id
      firstName
      lastName
    }
    actor {
      id
      name
      surname
    }
  }
}
```