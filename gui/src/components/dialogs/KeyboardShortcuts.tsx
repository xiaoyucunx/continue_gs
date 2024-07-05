import ReactDOM from "react-dom";
import styled from "styled-components";
import {
  StyledTooltip,
  defaultBorderRadius,
  lightGray,
  vscForeground,
} from "..";
import { getPlatform, isJetBrains } from "../../util";

const GridDiv = styled.div`
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  grid-gap: 2rem;
  padding: 1rem;
  justify-items: center;
  align-items: center;

  border-top: 0.5px solid ${lightGray};
`;

const StyledKeyDiv = styled.div`
  border: 0.5px solid ${lightGray};
  border-radius: ${defaultBorderRadius};
  padding: 4px;
  color: ${vscForeground};

  width: 16px;
  height: 16px;

  display: flex;
  justify-content: center;
  align-items: center;
`;

const keyToName = {
  "⌘": "Cmd",
  "⌃": "Ctrl",
  "⇧": "Shift",
  "⏎": "Enter",
  "⌫": "Backspace",
  "⌥": "Option",
  "⎇": "Alt",
};

function KeyDiv({ text }: { text: string }) {
  const tooltipPortalDiv = document.getElementById("tooltip-portal-div");

  return (
    <>
      <StyledKeyDiv data-tooltip-id={`header_button_${text}`}>
        {text}
      </StyledKeyDiv>
      {tooltipPortalDiv &&
        ReactDOM.createPortal(
          <StyledTooltip id={`header_button_${text}`} place="bottom">
            {keyToName[text]}
          </StyledTooltip>,
          tooltipPortalDiv,
        )}
    </>
  );
}

interface KeyboardShortcutProps {
  mac: string;
  windows: string;
  description: string;
}

function KeyboardShortcut(props: KeyboardShortcutProps) {
  const shortcut = getPlatform() === "mac" ? props.mac : props.windows;
  return (
    <div className="flex justify-between w-full items-center">
      <span
        style={{
          color: vscForeground,
        }}
      >
        {props.description}
      </span>
      <div className="flex gap-2 float-right">
        {shortcut.split(" ").map((key, i) => {
          return <KeyDiv key={i} text={key}></KeyDiv>;
        })}
      </div>
    </div>
  );
}

const vscodeShortcuts: KeyboardShortcutProps[] = [
  {
    "mac": "⌘ L",
    "windows": "⌃ L",
    "description": "选择代码 + 新会话"
  },
  {
    "mac": "⌘ I",
    "windows": "⌃ I",
    "description": "编辑突出显示的代码"
  },
  {
    "mac": "⌘ ⇧ L",
    "windows": "⌃ ⇧ L",
    "description": "选择代码"
  },
  {
    "mac": "⌘ ⇧ ⏎",
    "windows": "⌃ ⇧ ⏎",
    "description": "接受差异"
  },
  {
    "mac": "⌘ ⇧ ⌫",
    "windows": "⌃ ⇧ ⌫",
    "description": "拒绝差异"
  },
  {
    "mac": "⌥ ⌘ Y",
    "windows": "Alt ⌃ Y",
    "description": "接受差异中的顶部更改"
  },
  {
    "mac": "⌥ ⌘ N",
    "windows": "Alt ⌃ N",
    "description": "拒绝差异中的顶部更改"
  },
  {
    "mac": "⌥ ⌘ L",
    "windows": "Alt ⌃ L",
    "description": "切换继续侧边栏"
  },
  {
    "mac": "⌘ ⇧ R",
    "windows": "⌃ ⇧ R",
    "description": "调试终端"
  },
  {
    "mac": "⌘ ⌫",
    "windows": "⌃ ⌫",
    "description": "取消响应"
  },
  {
    "mac": "⌘ K ⌘ M",
    "windows": "⌃ K ⌃ M",
    "description": "切换全屏"
  },
  {
    "mac": "⌘ '",
    "windows": "⌃ '",
    "description": "切换所选模型"
  }
];

const jetbrainsShortcuts: KeyboardShortcutProps[] = [
  {
    "mac": "⌘ J",
    "windows": "⌃ J",
    "description": "选择代码 + 新会话"
  },
  {
    "mac": "⌘ ⇧ J",
    "windows": "⌃ ⇧ J",
    "description": "选择代码"
  },
  {
    "mac": "⌘ I",
    "windows": "⌃ I",
    "description": "编辑突出显示的代码"
  },
  {
    "mac": "⌘ ⇧ I",
    "windows": "⌃ ⇧ I",
    "description": "切换内联编辑焦点"
  },
  {
    "mac": "⌘ ⇧ ⏎",
    "windows": "⌃ ⇧ ⏎",
    "description": "接受差异"
  },
  {
    "mac": "⌘ ⇧ ⌫",
    "windows": "⌃ ⇧ ⌫",
    "description": "拒绝差异"
  },
  {
    "mac": "⌥ ⇧ J",
    "windows": "Alt ⇧ J",
    "description": "快速输入"
  },
  {
    "mac": "⌥ ⌘ J",
    "windows": "Alt ⌃ J",
    "description": "切换侧边栏"
  },
  {
    "mac": "⌘ ⌫",
    "windows": "⌃ ⌫",
    "description": "取消响应"
  },
  {
    "mac": "⌘ '",
    "windows": "⌃ '",
    "description": "切换所选模型"
  }
];

function KeyboardShortcutsDialog() {
  return (
    <div className="p-2">
      <h3 className="my-3 mx-auto text-center">快捷键</h3>
      <GridDiv>
        {(isJetBrains() ? jetbrainsShortcuts : vscodeShortcuts).map(
          (shortcut, i) => {
            return (
              <KeyboardShortcut
                key={i}
                mac={shortcut.mac}
                windows={shortcut.windows}
                description={shortcut.description}
              />
            );
          },
        )}
      </GridDiv>
    </div>
  );
}

export default KeyboardShortcutsDialog;
