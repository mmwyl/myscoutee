portal_db = db.getSiblingDB("myscoutee_db");
portal_db.createCollection("profiles");
portal_db.createCollection("cars");
portal_db.createCollection("events");
portal_db.createCollection("items");
portal_db.createCollection("feedbacks");
portal_db.createCollection("users");
portal_db.createCollection("groups");
portal_db.createCollection("roles");
portal_db.createCollection("messages");
portal_db.createCollection("promotions");
portal_db.createCollection("ideas");
portal_db.createCollection("jobs");
portal_db.createCollection("settings");
portal_db.createCollection("forms");
portal_db.createCollection("tokens");
portal_db.createCollection("badges");
portal_db.createCollection("paths");
portal_db.createCollection("members");

portal_db.profiles.createIndex( { position : "2dsphere" } );

portal_db.events.createIndex( { position : "2dsphere" } );

portal_db.promotions.createIndex( { "item.info.position" : "2dsphere" } );

portal_db.groups.createIndex( { position : "2dsphere" } );

portal_db.createUser(
  {
    user: "guest",
    pwd: "guest",
    roles: [
      { role: "readWrite", db: "myscoutee_db" }
    ]
  });
