locals {
  # Repo
  github = {
    org        = "pagopa"
    repository = "arpu-be"
  }

  repo_secrets = var.env_short == "p" ? {
    SONAR_TOKEN       = data.azurerm_key_vault_secret.sonar_token[0].value
    SLACK_WEBHOOK_URL = data.azurerm_key_vault_secret.slack_webhook[0].value
  } : {}

  map_repo = {
    "dev" : "*",
    "uat" : "uat"
    "prod" : "main"
  }

  branches                  = ["develop", "uat"]
  bypass_branch_rules_teams = ["p4pa-admins", "payments-cloud-admin"]

  # this is use to lookup the id for each team
  team_name_to_id = {
    for team in data.github_organization_teams.all.teams :
    team.name => team.id
  }

  branch_rulesets = {
    develop = {
      ref_name                        = "refs/heads/develop"
      bypass_actors                   = false
      required_linear_history         = true
      require_code_owner_review       = false
      required_approving_review_count = 0
    }
    uat = {
      ref_name                        = "refs/heads/uat"
      bypass_actors                   = false
      required_linear_history         = false
      require_code_owner_review       = false
      required_approving_review_count = 1
    },
    main = {
      ref_name                        = "refs/heads/main"
      bypass_actors                   = false
      required_linear_history         = false
      require_code_owner_review       = true
      required_approving_review_count = 0
    },
  }
}
