package org.shkr.actors.play.encounter

/**
 * Copyright (c) 2015 Lumiata Inc.
 */
object StatementUtil {

  val HearPattern = "@([a-zA-Z0-9_\\.]+)\\s+ says\\s+(.+?)".r
  val TellPattern = "@([a-zA-Z0-9_\\.]+)\\s*(.+?)".r
}
