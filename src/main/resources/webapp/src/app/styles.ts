import { createStyles, Theme } from "@material-ui/core";

export const styles = ({ spacing }: Theme) =>
  createStyles({
    heroesList: {},
    paper: {
      padding: spacing.unit,
      margin: spacing.unit * 3
    }
  });
