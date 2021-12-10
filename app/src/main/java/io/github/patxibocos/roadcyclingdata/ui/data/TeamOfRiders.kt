package io.github.patxibocos.roadcyclingdata.ui.data

import io.github.patxibocos.pcsscraper.protobuf.RiderOuterClass.Rider
import io.github.patxibocos.pcsscraper.protobuf.TeamOuterClass.Team

class TeamOfRiders(val team: Team, val riders: List<Rider>)
