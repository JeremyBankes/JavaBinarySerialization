# Serialization

A super simple Java serialization library. This library allows for packing information into Elements, Batches and Capsules (smallest to largest, respectively) which can both be serialized into a custom, compact binary format.

This can be useful for sending structured information over a network, saving it to disk or any other scenerio where it may be benifical to represent information in a small as possible footprint.

## The Element
```com.sineshore.serialization.Element```

---
An element represents one piece of information and provides an interface to convert it into bytes. For simplicity sake, an element typically wraps a primitive. This is not a regulation though. Objects can be serialized as an element. However, when they are deserialized, they will become DeserializedObject instances rather than an instance of their original class.

``` Java
Element name = new Element("name", "Jeremy");
Element age = new Element("age", 20);

age.getName()   // String   : "age"
age.getSize()   // short    : size of element in bytes
age.getBytes()  // byte[]   : The serialized element 
```

## The Batch
```com.sineshore.serialization.Batch```

---
A batch is a key-value pair collection of Elements that can be converted to and from bytes.

### Creating a Batch
``` Java
// Creating a batch from scratch
// -----------------------------
Batch personBatch = new Batch("Jeremy");

personBatch.add("age", 20);
// Is equivalent to
// personBatch.add(new Element("age", 20));
personBatch.add("gender", true);
personBatch.add("from", "Canada");


// Creating a batch from bytes
// ---------------------------
byte[] serializedData = ...
Batch personBatch = new Batch(serializedData);


// Creating a batch from an input stream
// ---------------------------
InputStream serializedDataInputStream = ...
Batch personBatch = new Batch(serializedDataInputStream);
```

### Using a Batch
``` Java
// Reading information from a batch
// --------------------------------
int age = personBatch.get("age", Integer.class);
boolean gender = personBatch.get("gender", Boolean.class);
Element fromElement = personBatch.getElement("from");

fromElement.getName() // String - "from"
String from = (String) fromElement.getValue() // Object (String) - "Canada"


// Serialize a batch
// -----------------
byte[] serializedBatch = personBatch.toBytes();
```

## The Capsule
```com.sineshore.serialization.Capsule```

---
A capsule is a key-value collection of Batches that can be converted to and from bytes.

### Creating a Capsule
``` Java
// Creating a capsule from scratch
// -------------------------------
Capsule peopleCapsule = new Capsule("People");

peopleCapsule.add(personBatch);


// Creating a capsule from bytes
// ---------------------------
byte[] serializedData = ...
Capsule capsule = new Capsule(serializedData);

// Creating a batch from an input stream
// ---------------------------
InputStream serializedDataInputStream = ...
Capsule capsule = new Capsule(serializedDataInputStream);
```

### Using a Batch
``` Java
// Reading information from a capsule
// ----------------------------------
Batch personBatch = peopleCapsule.getBatch("Jeremy");
// See "Using a Batch" to see how to read from personBatch

// Serialize a Capsule
// -------------------
byte[] serializedCapsule = peopleCapsule.toBytes();
```