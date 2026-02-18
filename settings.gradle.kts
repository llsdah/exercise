rootProject.name = "exercise"
include("algorithm")
include("zerobase-3rd")
include("zerobase-3rd:data-structure-and-algorithm")
findProject(":zerobase-3rd:data-structure-and-algorithm")?.name = "data-structure-and-algorithm"
include("fastcampus")
include("fastcampus:sns-service")
findProject(":fastcampus:sns-service")?.name = "sns-service"
include("fastcampus:loan-project")
findProject(":fastcampus:loan-project")?.name = "loan-project"
