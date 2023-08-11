report_message="# Snapshot tests failures:"
for delta_file in $(ls "$DIFFS_PATH" | grep ^delta); do
  request_path=https://api.github.com/repos/patxibocos/assets/contents/my-cyclist/$COMMIT_SHA/$delta_file
  base64_content=$(base64 -w0 "$DIFFS_PATH/$delta_file")
  commit_message="Upload $delta_file"
  curl -L \
    -X PUT \
    -H "Accept: application/vnd.github+json" \
    -H "Authorization: Bearer $ASSETS_TOKEN" \
    -H "X-GitHub-Api-Version: 2022-11-28" \
    "$request_path" \
    -d "{\"message\":\"$commit_message\",\"committer\":{\"name\":\"Patxi Bocos\",\"email\":\"patxi952@gmail.com\"},\"content\":\"$base64_content\"}"
  image_url="https://raw.githubusercontent.com/patxibocos/assets/main/my-cyclist/$COMMIT_SHA/$delta_file"
  image_markdown="![$delta_file]($image_url)"
  test_name=$(sed 's/.*delta-\(.*\).png.*/\1/' <<<"$delta_file")
  report_message+="
  #### $test_name
  $image_markdown"
done
{
  echo 'SNAPSHOT_REPORT_MESSAGE<<EOF'
  echo "$report_message"
  echo 'EOF'
} >>"$GITHUB_OUTPUT"
