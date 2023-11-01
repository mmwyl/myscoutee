/* global use, db */
// MongoDB Playground
// To disable this template go to Settings | MongoDB | Use Default Template For Playground.
// Make sure you are connected to enable completions and to be able to run a playground.
// Use Ctrl+Space inside a snippet or a string literal to trigger completions.
// The result of the last command run in a playground is shown on the results panel.
// By default the first 20 documents will be returned with a cursor.
// Use 'console.log()' to print to the debug output.
// For more documentation on playgrounds please refer to
// https://www.mongodb.com/docs/mongodb-vscode/playgrounds/

// Select the database to use.
use('myscoutee_db');

// Here we run an aggregation and open a cursor to the results.
// Use '.toArray()' to exhaust the cursor to return the whole result set.
// You can use '.hasNext()/.next()' to iterate through the cursor page by page.
db.getCollection('profiles').aggregate([
    {
        "$geoNear": {
            "near": {
                "type": "Point", "coordinates": [19.1149536,
                    47.4685519]
            },
            "spherical": true,
            "minDistance": 0.0,
            "distanceField": "distance",
            "query": {
                "_id": {
                    "$nin": [
                        BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                    ]
                },
                "gender": "m",
                "group": BinData(3, "PkoHFjkq6xouVW/lzz01pQ=="),
                "status": "A",
                "birthday": {
                    "$gte": new Date(-1456926546880),
                    "$lte": new ISODate("2005-11-01T10:10:53.121Z")
                }
            }
        }
    },
    {
        "$lookup": {
            "from": "likes",
            "let": {
                "p_id": "$_id",
                "p_score": "$score"
            },
            "pipeline": [
                {
                    "$match": {
                        "$expr": {
                            "$and": [
                                {
                                    "$eq": [
                                        "$type",
                                        "P"
                                    ]
                                },
                                {
                                    "$or": [
                                        {
                                            "$and": [
                                                {
                                                    "$ne": [
                                                        null,
                                                        null
                                                    ]
                                                },
                                                {
                                                    "$and": [
                                                        {
                                                            "$or": [
                                                                {
                                                                    "$eq": [
                                                                        "$from.$id",
                                                                        "$$p_id"
                                                                    ]
                                                                },
                                                                {
                                                                    "$eq": [
                                                                        "$to.$id",
                                                                        "$$p_id"
                                                                    ]
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "$ne": [
                                                                "$from.$id",
                                                                BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                            ]
                                                        },
                                                        {
                                                            "$ne": [
                                                                "$to.$id",
                                                                BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                            ]
                                                        },
                                                        {
                                                            "$eq": [
                                                                "$createdBy.$id",
                                                                BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                            ]
                                                        }
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            "$and": [
                                                {
                                                    "$eq": [
                                                        "$from.$id",
                                                        "$$p_id"
                                                    ]
                                                },
                                                {
                                                    "$eq": [
                                                        "$to.$id",
                                                        BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                    ]
                                                }
                                            ]
                                        },
                                        {
                                            "$and": [
                                                {
                                                    "$eq": [
                                                        "$to.$id",
                                                        "$$p_id"
                                                    ]
                                                },
                                                {
                                                    "$eq": [
                                                        "$from.$id",
                                                        BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                    ]
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    }
                },
                {
                    "$group": {
                        "_id": null,
                        "distance": {
                            "$avg": "$distance"
                        },
                        "ref": {
                            "$first": {
                                "$ifNull": [
                                    "$ref",
                                    0
                                ]
                            }
                        },
                        "direction": {
                            "$avg": {
                                "$switch": {
                                    "branches": [
                                        {
                                            "case": {
                                                "$and": [
                                                    {
                                                        "$eq": [
                                                            "$to.$id",
                                                            BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                        ]
                                                    },
                                                    {
                                                        "$in": [
                                                            "$status",
                                                            [
                                                                "A"
                                                            ]
                                                        ]
                                                    }
                                                ]
                                            },
                                            "then": 2
                                        },
                                        {
                                            "case": {
                                                "$and": [
                                                    {
                                                        "$eq": [
                                                            "$from.$id",
                                                            BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                        ]
                                                    },
                                                    {
                                                        "$in": [
                                                            "$status",
                                                            [
                                                                "A"
                                                            ]
                                                        ]
                                                    }
                                                ]
                                            },
                                            "then": 1
                                        }
                                    ],
                                    "default": 0
                                }
                            }
                        },
                        "rateFrom": {
                            "$first" : {
                            "$cond": {
                              "if": {
                                "$and": [
                                  {
                                    "$eq": [
                                      "$from.$id",
                                      BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                    ]
                                  },
                                  {
                                    "$in": [
                                      "$status",
                                      ["A"]
                                    ]
                                  }
                                ]
                              },
                              "then": "$rate",
                              "else": null
                            }
                        }
                        },
                        "rateAPG": {
                            "$sum": {
                                "$switch": {
                                    "branches": [
                                        {
                                            "case": {
                                                "$and": [
                                                    {
                                                        "$in": [
                                                            "$status",
                                                            [
                                                                "A"
                                                            ]
                                                        ]
                                                    }
                                                ]
                                            },
                                            "then": {
                                                "$divide": [
                                                    1,
                                                    "$rate"
                                                ]
                                            }
                                        }
                                    ],
                                    "default": 0
                                }
                            }
                        },
                        "avgRateD": {
                            "$avg": {
                                "$switch": {
                                    "branches": [
                                        {
                                            "case": {
                                                "$and": [
                                                    {
                                                        "$eq": [
                                                            "$status",
                                                            "D"
                                                        ]
                                                    }
                                                ]
                                            },
                                            "then": "$rate"
                                        }
                                    ],
                                    "default": 0
                                }
                            }
                        },
                        "numD": {
                            "$sum": {
                                "$cond": [
                                    {
                                        "$in": [
                                            "$status",
                                            [
                                                "D"
                                            ]
                                        ]
                                    },
                                    1,
                                    0
                                ]
                            }
                        },
                        "numAPG": {
                            "$sum": {
                                "$cond": [
                                    {
                                        "$in": [
                                            "$status",
                                            [
                                                "A"
                                            ]
                                        ]
                                    },
                                    1,
                                    0
                                ]
                            }
                        },
                        "createdDate": {
                            "$max": "$createdDate"
                        }
                    }
                },
                {
                    "$set": {
                        "numD": {
                            "$cond": {
                                "if": {
                                    "$lt": [
                                        "$numD",
                                        1
                                    ]
                                },
                                "then": 1,
                                "else": {
                                    "$cond": {
                                        "if": {
                                            "$gt": [
                                                "$numD",
                                                50
                                            ]
                                        },
                                        "then": 50,
                                        "else": "$numD"
                                    }
                                }
                            }
                        }
                    }
                },
                {
                    "$set": {
                        "numD": {
                            "$divide": [
                                {
                                    "$ln": {
                                        "$add": [
                                            "$numD",
                                            1
                                        ]
                                    }
                                },
                                {
                                    "$ln": {
                                        "$add": [
                                            50,
                                            1
                                        ]
                                    }
                                }
                            ]
                        }
                    }
                },
                {
                    "$set": {
                        "rate": {
                            "$add": [
                                {
                                    "$divide": [
                                        "$numAPG",
                                        "$rateAPG"
                                    ]
                                },
                                {
                                    "$divide": [
                                        {
                                            "$multiply": [
                                                "$avgRateD",
                                                "$numD"
                                            ]
                                        },
                                        10
                                    ]
                                }
                            ]
                        }
                    }
                }
            ],
            "as": "likes"
        }
    },
    {
        "$unwind": {
            "path": "$likes",
            "preserveNullAndEmptyArrays": true
        }
    },
    {
        "$set": {
            "likes.direction": {
                "$ifNull": [
                    "$likes.direction",
                    0
                ]
            },
            "likes.rate": {
                "$ifNull": [
                    "$likes.rate",
                    0
                ]
            },
            "likes.rateFrom": {
                "$ifNull": [
                    "$likes.rateFrom",
                    0
                ]
            },
            "likes.distance": {
                "$ifNull": [
                    "$likes.distance",
                    {
                        "$abs": {
                            "$subtract": [
                                "$score",
                                10.0
                            ]
                        }
                    }
                ]
            },
            "createdDate": {
                "$ifNull": [
                    "$createdDate",
                    new ISODate("2023-11-01T12:25:04.093Z")
                ]
            }
        }
    },
    {
        "$addFields": {
            "groupKey": {
                "$multiply": [
                    {
                        "$floor": {
                            "$divide": [
                                "$distance",
                                5
                            ]
                        }
                    },
                    5
                ]
            }
        }
    },
    /*{
        "$match": {
            "$expr": {
                "$and": [
                    {
                        "$or": [
                            {
                                "$ne": [
                                    null,
                                    null
                                ]
                            },
                            {
                                "$eq": [
                                    "$likes.direction",
                                    1.5
                                ]
                            }
                        ]
                    },
                    {
                        "$or": [
                            {
                                "$gt": [
                                    "$groupKey",
                                    0.0
                                ]
                            },
                            {
                                "$and": [
                                    {
                                        "$eq": [
                                            "$groupKey",
                                            0.0
                                        ]
                                    },
                                    {
                                        "$or": [
                                            {
                                                "$lt": [
                                                    "$likes.rate",
                                                    10.0
                                                ]
                                            },
                                            {
                                                "$and": [
                                                    {
                                                        "$eq": [
                                                            "$likes.rate",
                                                            10.0
                                                        ]
                                                    },
                                                    {
                                                        "$or": [
                                                            {
                                                                "$gt": [
                                                                    "$likes.distance",
                                                                    0.0
                                                                ]
                                                            },
                                                            {
                                                                "$and": [
                                                                    {
                                                                        "$eq": [
                                                                            "$likes.distance",
                                                                            0.0
                                                                        ]
                                                                    },
                                                                    {
                                                                        "$lt": [
                                                                            "$createdDate",
                                                                            new ISODate("2023-11-01T00:00:00Z")
                                                                        ]
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                ]
                                            }
                                        ]
                                    }
                                ]
                            }
                        ]
                    }
                ]
            }
        }
    },
    {
        "$lookup": {
            "from": "events",
            "let": {
                "p_id": "$_id"
            },
            "pipeline": [
                {
                    "$match": {
                        "status": "A",
                        "$expr": {
                            "$and": [
                                {
                                    "$eq": [
                                        {
                                            "$size": {
                                                "$filter": {
                                                    "input": "$members",
                                                    "cond": {
                                                        "$or": [
                                                            {
                                                                "$and": [
                                                                    {
                                                                        "$eq": [
                                                                            "$$this.status",
                                                                            "A"
                                                                        ]
                                                                    },
                                                                    {
                                                                        "$eq": [
                                                                            "$$this.profile.$id",
                                                                            "$$p_id"
                                                                        ]
                                                                    }
                                                                ]
                                                            },
                                                            {
                                                                "$and": [
                                                                    {
                                                                        "$eq": [
                                                                            "$$this.status",
                                                                            "A"
                                                                        ]
                                                                    },
                                                                    {
                                                                        "$eq": [
                                                                            "$$this.profile.$id",
                                                                            BinData(3, "qkqdQP7vzFQuTdpr1xojmg==")
                                                                        ]
                                                                    }
                                                                ]
                                                            }
                                                        ]
                                                    }
                                                }
                                            }
                                        },
                                        2
                                    ]
                                }
                            ]
                        }
                    }
                },
                {
                    "$sort": {
                        "range.start": 1
                    }
                },
                {
                    "$limit": 1
                }
            ],
            "as": "events"
        }
    },
    {
        "$unwind": {
            "path": "$events",
            "preserveNullAndEmptyArrays": true
        }
    },
    {
        "$addFields": {
            "met": {
                "$cond": {
                    "if": {
                        "$not": {
                            "$in": [
                                {
                                    "$ifNull": [
                                        "$events",
                                        null
                                    ]
                                },
                                [
                                    null,
                                    []
                                ]
                            ]
                        }
                    },
                    "then": true,
                    "else": false
                }
            }
        }
    },
    {
        "$match": {
            "$expr": {
                "$or": [
                    {
                        "$and": [
                            {
                                "$ne": [
                                    1.5,
                                    1.5
                                ]
                            },
                            {
                                "$eq": [
                                    false,
                                    "$met"
                                ]
                            }
                        ]
                    },
                    {
                        "$and": [
                            {
                                "$eq": [
                                    1.5,
                                    1.5
                                ]
                            },
                            {
                                "$eq": [
                                    false,
                                    "$met"
                                ]
                            }
                        ]
                    }
                ]
            }
        }
    },
    {
        "$sort": {
            "groupKey": 1,
            "likes.rate": -1,
            "likes.distance": 1,
            "createdDate": -1
        }
    },
    {
        "$limit": 20
    },
    {
        "$addFields": {
            "likes.rate": {
                "$ifNull": [
                    "$likes.rate",
                    0
                ]
            }
        }
    },*/
    {
        "$replaceRoot": {
            "newRoot": {
                "$mergeObjects": [
                    {
                        "profile": "$$ROOT"
                    },
                    {
                        "rate": {
                            "$cond": {
                                "if": {
                                    "$ne": [
                                        2.0,
                                        1.5
                                    ]
                                },
                                "then": "$likes.rateFrom",
                                "else": 0
                            }
                        },
                        "groupKey": "$groupKey",
                        "met": "$met",
                        "offset": [
                            "$groupKey",
                            "$likes.rate",
                            "$likes.distance",
                            "$createdDate"
                        ]
                    }
                ]
            }
        }
    }
]);
