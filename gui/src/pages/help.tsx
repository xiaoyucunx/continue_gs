import { ArrowLeftIcon } from "@heroicons/react/24/outline";
import { useContext } from "react";
import { useNavigate } from "react-router-dom";
import styled from "styled-components";
import {
  Button,
  Hr,
  lightGray,
  vscBackground,
  vscForeground,
} from "../components";
import KeyboardShortcutsDialog from "../components/dialogs/KeyboardShortcuts";
import { IdeMessengerContext } from "../context/IdeMessenger";
import { useNavigationListener } from "../hooks/useNavigationListener";

const ResourcesDiv = styled.div`
  margin: 4px;
  border-top: 0.5px solid ${lightGray};
  border-bottom: 0.5px solid ${lightGray};
`;

const IconDiv = styled.div<{ backgroundColor?: string }>`
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;

  padding: 12px;

  & > a {
    color: ${vscForeground};
    text-decoration: none;
    display: flex;
    align-items: center;
    width: 100%;
    justify-content: center;
  }

  &:hover {
    background-color: ${(props) => props.backgroundColor || lightGray};
  }
`;

const TutorialButton = styled(Button)`
  padding: 2px 4px;
  margin-left: auto;
  margin-right: 12px;
  background-color: transparent;
  color: ${vscForeground};
  border: 1px solid ${lightGray};
  &:hover {
    background-color: ${lightGray};
  }
`;

function HelpPage() {
  useNavigationListener();
  const navigate = useNavigate();
  const ideMessenger = useContext(IdeMessengerContext);

  return (
    <div className="overflow-y-scroll overflow-x-hidden">
      <div
        className="items-center flex m-0 p-0 sticky top-0"
        style={{
          borderBottom: `0.5px solid ${lightGray}`,
          backgroundColor: vscBackground,
        }}
      >
        <ArrowLeftIcon
          width="1.2em"
          height="1.2em"
          onClick={() => navigate("/")}
          className="inline-block ml-4 cursor-pointer"
        />
        <h3 className="text-lg font-bold m-2 inline-block">Help Center</h3>
        <TutorialButton
          onClick={() => {
            ideMessenger.post("showTutorial", undefined);
            navigate("/onboarding");
          }}
        >
          Open tutorial
        </TutorialButton>
      </div>

      <h3
        className="my-0 py-3 mx-auto text-center cursor-pointer"
        onClick={() => {
          navigate("/stats");
        }}
      >
        使用情况
      </h3>
      <ResourcesDiv className="border">
        <IconDiv backgroundColor={"#1bbe84a8"}>
          <a
            href="https://docs.continue.dev/how-to-use-continue"
            target="_blank"
          >
            <svg
              width="42px"
              height="42px"
              xmlns="http://www.w3.org/2000/svg"
              viewBox="-2.2 -2 28 28"
              fill={vscForeground}
            >
              <path d="M11.25 4.533A9.707 9.707 0 006 3a9.735 9.735 0 00-3.25.555.75.75 0 00-.5.707v14.25a.75.75 0 001 .707A8.237 8.237 0 016 18.75c1.995 0 3.823.707 5.25 1.886V4.533zM12.75 20.636A8.214 8.214 0 0118 18.75c.966 0 1.89.166 2.75.47a.75.75 0 001-.708V4.262a.75.75 0 00-.5-.707A9.735 9.735 0 0018 3a9.707 9.707 0 00-5.25 1.533v16.103z" />
            </svg>
            使用说明
          </a>
        </IconDiv>
      </ResourcesDiv>

      <KeyboardShortcutsDialog />
    </div>
  );
}

export default HelpPage;
