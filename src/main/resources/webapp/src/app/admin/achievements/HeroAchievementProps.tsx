import { Hero, HeroAchievementDetail } from "../../../services/api";

export interface HeroAchievementProps {
  hero: Hero;
  achievement?: any;
  onSave(o: HeroAchievementDetail): void;
}
