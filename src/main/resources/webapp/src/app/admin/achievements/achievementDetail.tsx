import React from "react";
import { Achievement } from "../../../services/api";
import { BoardMemberAchievementDetails } from "./BoardMemberAchievementDetails";
import { EmptyAchievementDetails } from "./EmptyAchievementDetails";
import { HeroAchievementProps } from "./HeroAchievementProps";
import { JavaBinSpeakerAchievementDetails } from "./JavaBinSpeakerAchievementDetails";
import { JavaZoneSpeakerAchievementDetails } from "./JavaZoneSpeakerAchievementDetails";

export function achievementDetail(achievementType?: Achievement): React.ComponentType<HeroAchievementProps> {
  switch (achievementType) {
    case "FOREDRAGSHOLDER_JAVABIN":
      return JavaBinSpeakerAchievementDetails;
    case "FOREDRAGSHOLDER_JZ":
      return JavaZoneSpeakerAchievementDetails;
    case "STYRE":
      return BoardMemberAchievementDetails;
    case undefined:
      return EmptyAchievementDetails;
  }
}
