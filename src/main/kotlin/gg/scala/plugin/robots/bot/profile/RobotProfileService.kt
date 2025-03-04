package gg.scala.plugin.robots.bot.profile

import gg.scala.commons.persist.ProfileOrchestrator
import gg.scala.flavor.service.Service
import java.util.*

@Service
object RobotProfileService : ProfileOrchestrator<RobotProfile>()
{
    override fun new(uniqueId: UUID) = RobotProfile(uniqueId)
    override fun type() = RobotProfile::class

    fun findOrCreate(uuid: UUID): RobotProfile = find(uuid) ?: new(uuid)
}
