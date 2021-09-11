package io.github.patxibocos.roadcyclingdata.ui.data

import io.github.patxibocos.pcsscraper.protobuf.rider.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.team.TeamOuterClass.Team

class TeamOfRiders(val team: Team, val riders: List<Rider>)
